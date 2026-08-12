package com.boothly.backend.service;

import com.boothly.backend.common.exception.order.InvalidOrderStatusTransitionException;
import com.boothly.backend.common.exception.order.OrderCreationException;
import com.boothly.backend.common.exception.order.OrderItemMissingForOrderException;
import com.boothly.backend.common.exception.order.OrderNotFoundException;
import com.boothly.backend.common.exception.salepost.*;
import com.boothly.backend.common.exception.user.UserNotFoundException;
import com.boothly.backend.common.generator.OrderNumberGenerator;
import com.boothly.backend.domain.enums.OrderStatus;
import com.boothly.backend.domain.enums.SalePostStatus;
import com.boothly.backend.domain.order.Order;
import com.boothly.backend.domain.order.OrderItem;
import com.boothly.backend.domain.salepost.DeliveryMethod;
import com.boothly.backend.domain.salepost.ProductOption;
import com.boothly.backend.domain.salepost.SalePost;
import com.boothly.backend.domain.user.User;
import com.boothly.backend.dto.order.OrderStatusChangeResponse;
import com.boothly.backend.dto.order.create.OrderCreateRequest;
import com.boothly.backend.dto.order.create.OrderCreateResponse;
import com.boothly.backend.dto.order.create.OrderItemCreateRequest;
import com.boothly.backend.dto.order.detail.OrderDetailResponse;
import com.boothly.backend.dto.order.list.OrderListResponse;
import com.boothly.backend.dto.order.list.SellerSalePostOrderResponse;
import com.boothly.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final SalePostRepository salePostRepository;
    private final ProductOptionRepository productOptionRepository;
    private final DeliveryMethodRepository deliveryMethodRepository;
    private final OrderNumberGenerator orderNumberGenerator;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public OrderCreateResponse createOrder(Long buyerId, Long salePostId, OrderCreateRequest request) {
        User buyer = userRepository.findById(buyerId).orElseThrow(() ->
                new UserNotFoundException(buyerId));
        SalePost salePost = getOpenSalePostForOrderCreation(salePostId);
        Map<Long, ProductOption> productOptionsById = getProductOptionsByIdForOrderCreation(salePostId, request.getOrderItems());
        DeliveryMethod deliveryMethod = getActiveDeliveryMethodForOrderCreation(salePostId, request.getDeliveryMethodId());
        int itemsTotalPrice = calculateItemsTotalPrice(productOptionsById, request.getOrderItems());

        Order order = createNewOrder(buyer, salePost, deliveryMethod, request, itemsTotalPrice);
        Order savedOrder = orderRepository.save(order);

        decreaseProductOptionStocks(productOptionsById, request.getOrderItems());
        List<OrderItem> orderItems = createOrderItems(savedOrder, productOptionsById, request.getOrderItems());
        List<OrderItem> savedOrderItems = orderItemRepository.saveAll(orderItems);

        return OrderCreateResponse.from(savedOrder, savedOrderItems);
    }

    public Page<OrderListResponse> findBuyerOrders(
            Long buyerId,
            String keyword,
            Pageable pageable) {

        return orderRepository.findByBuyer_IdAndSalePost_TitleContaining(buyerId, keyword, pageable)
                .map(OrderListResponse::from);
    }

    public OrderDetailResponse findBuyerOrderDetail(Long orderId, Long buyerId) {
        Order order = orderRepository.findByIdAndBuyer_Id(orderId, buyerId).orElseThrow(() ->
                new OrderNotFoundException(buyerId, orderId));
        List<OrderItem> orderItems = validateAndGetOrderItems(orderId);

        return  OrderDetailResponse.from(order, orderItems);
    }

    public Page<SellerSalePostOrderResponse> findSellerSalePostOrders(Long sellerId, Long salePostId, Pageable pageable) {
        validateSalePostOwner(sellerId, salePostId);

        Page<Order> orders = orderRepository.findBySalePost_Id(salePostId,pageable);

        Map<Long, List<OrderItem>> orderItems = orders.hasContent()
                ? validateAndGetOrderItemsByOrderId(orders.getContent())
                : Collections.emptyMap();

        return orders.map(order -> {
            Long orderId = order.getId();

            List<OrderItem> items = orderItems.get(orderId);

            if(items.isEmpty()){
                throw new OrderItemMissingForOrderException(orderId);
            }

            return SellerSalePostOrderResponse.from(order, items);
        });
    }

    @Transactional
    public OrderStatusChangeResponse changeOrderStatusBySeller(
            Long sellerId,
            Long salePostId,
            Long orderId,
            OrderStatus targetOrderStatus) {

        OrderStatus expectedCurrentStatus;
        switch (targetOrderStatus) {
            case CONFIRMED, CANCELED -> expectedCurrentStatus = OrderStatus.REQUESTED;
            case REQUESTED, COMPLETED -> expectedCurrentStatus = OrderStatus.CONFIRMED;
            default -> throw new InvalidOrderStatusTransitionException(
                    orderRepository.findBySalePost_User_IdAndSalePost_idAndId(sellerId, salePostId, orderId)
                            .orElseThrow(()->new OrderNotFoundException(sellerId, salePostId, orderId))
                            .getOrderStatus(),
                    targetOrderStatus
            );
        }

        if( targetOrderStatus == OrderStatus.CANCELED ){
            lockSalePost(salePostId);
        }

        int updated = orderRepository.changeOrderStatusBySeller(sellerId, salePostId, orderId, expectedCurrentStatus, targetOrderStatus);
        if(updated == 0){
            throw new InvalidOrderStatusTransitionException(
                    orderRepository.findBySalePost_User_IdAndSalePost_idAndId(sellerId, salePostId, orderId)
                            .orElseThrow(()->new OrderNotFoundException(sellerId, salePostId, orderId))
                            .getOrderStatus(),
                    targetOrderStatus
            );
        }

        if( targetOrderStatus == OrderStatus.CANCELED ){
            restoreStock(salePostId, orderId);
        }

        return OrderStatusChangeResponse.from(
               orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(sellerId, orderId))
        );
    }

    @Transactional
    public OrderStatusChangeResponse cancelOrderStatusByBuyer(
            Long buyerId,
            Long orderId
    ){
        Order order = orderRepository.findByBuyer_IdAndId(buyerId, orderId)
                .orElseThrow(()->new OrderNotFoundException(buyerId, orderId));

        Long salePostId = order.getSalePost().getId();
        lockSalePost(salePostId);

        int updated = orderRepository.cancelRequestedOrderByBuyer(buyerId, orderId);

        if(updated == 0){
            Order currentOrder = orderRepository.findByBuyer_IdAndId(buyerId, orderId)
                    .orElseThrow(() -> new OrderNotFoundException(buyerId, orderId));

            throw new InvalidOrderStatusTransitionException(
                    currentOrder.getOrderStatus(),
                    OrderStatus.CANCELED
            );
        }

        restoreStock(salePostId, orderId);

        return OrderStatusChangeResponse.from(
                orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(buyerId, orderId))
        );
    }


    private void lockSalePost(Long salePostId) {
        salePostRepository.findForUpdateById(salePostId)
                .orElseThrow(() -> new SalePostNotFoundException(salePostId));
    }

    private SalePost getOpenSalePostForOrderCreation(Long salePostId) {
        SalePost salePost = salePostRepository
                .findForCreateOrderById(salePostId)
                .orElseThrow(() -> new SalePostNotFoundException(salePostId));

        if (salePost.getSaleStatus() != SalePostStatus.OPEN) {
            throw new SalePostNotOpenException(salePostId);
        }

        return salePost;
    }


    private Map<Long, ProductOption> getProductOptionsByIdForOrderCreation(Long salePostId, List<OrderItemCreateRequest> orderItems) {
        // 1. 중복된 상품 옵션이 있는지 검증
        Set<Long> uniqueOptionIds = orderItems.stream()
                .map(OrderItemCreateRequest::getProductOptionId)
                .collect(Collectors.toSet());

        if(orderItems.size() != uniqueOptionIds.size()) {
            throw new DuplicateProductOptionException();
        }

        // 2. 존재하지 않거나 다른 게시글의 옵션인지 검증
        List<ProductOption> matchedProductOptions = productOptionRepository.findAllBySalePost_IdAndDeletedAtIsNullAndIdIn(salePostId, uniqueOptionIds);

        if (matchedProductOptions.size() != uniqueOptionIds.size()) {
            throw new InvalidSalePostOptionException(salePostId, null);
        }

        return matchedProductOptions.stream()
                .collect(Collectors.toMap(ProductOption::getId, productOption -> productOption));
    }

    private DeliveryMethod getActiveDeliveryMethodForOrderCreation(Long salePostId, Long deliveryMethodId) {
        DeliveryMethod deliveryMethod = deliveryMethodRepository
                .findBySalePost_IdAndDeletedAtIsNullAndId(salePostId, deliveryMethodId)
                .orElseThrow(()->new DeliveryMethodNotFoundException(salePostId, deliveryMethodId));

        if(!deliveryMethod.isActive()){
            throw new InactiveDeliveryMethodException(salePostId, deliveryMethodId);
        }

        return deliveryMethod;
    }

    private int calculateItemsTotalPrice(Map<Long, ProductOption> productOptions, List<OrderItemCreateRequest> orderItems) {
        int itemsTotalPrice = 0;
        for(OrderItemCreateRequest request : orderItems) {
            ProductOption productOption = productOptions.get(request.getProductOptionId());
            itemsTotalPrice += productOption.getPrice() * request.getQuantity();
        }
        return itemsTotalPrice;
    }

    private Order createNewOrder(
            User buyer,
            SalePost salePost,
            DeliveryMethod deliveryMethod,
            OrderCreateRequest request,
            int itemsTotalPrice
    ) {
        return Order.builder()
                .buyer(buyer)
                .salePost(salePost)
                .payerName(request.getPayerName())
                .orderStatus(OrderStatus.REQUESTED)
                .orderNumber(generateUniqueOrderNumber())
                .recipientName(request.getRecipientName())
                .recipientPhone(request.getRecipientPhone())
                .deliveryType(deliveryMethod.getDeliveryType())
                .deliveryAddress(request.getDeliveryAddress())
                .deliveryMethodName(deliveryMethod.getMethodName())
                .deliveryFee(deliveryMethod.getDeliveryFee())
                .itemsTotalPrice(itemsTotalPrice)
                .memo(request.getMemo())
                .bankName(request.getBankName())
                .accountNumber(request.getAccountNumber())
                .accountHolder(request.getAccountHolder())
                .build();
    }

    private List<OrderItem> createOrderItems(
            Order order,
            Map<Long, ProductOption> productOptionsById,
            List<OrderItemCreateRequest> requests
    ) {
        List<OrderItem> newOrderItems = new ArrayList<>();

        for(OrderItemCreateRequest request : requests) {
            ProductOption productOption = productOptionsById.get(request.getProductOptionId());

            newOrderItems.add(OrderItem.create(order, productOption, request.getQuantity()));
        }

        return newOrderItems;
    }

    private void decreaseProductOptionStocks(
            Map<Long, ProductOption> productOptionsById,
            List<OrderItemCreateRequest> requests
    ) {
        for(OrderItemCreateRequest request : requests) {
            ProductOption productOption = productOptionsById.get(request.getProductOptionId());
            productOption.decreaseStock(request.getQuantity());
        }
    }

    private void restoreStock(Long salePostId, Long orderId){
        List<OrderItem> orderItems = validateAndGetOrderItems(orderId);

        for(OrderItem orderItem : orderItems){
            int restoreQuantity = orderItem.getQuantity();
            Long productOptionId = orderItem.getProductOption().getId();

            int update = productOptionRepository.restoreStock(
                    salePostId,
                    productOptionId,
                    restoreQuantity
            );

            if(update == 0){
                throw new InvalidSalePostOptionException(
                        salePostId,
                        orderItem.getProductOption().getId()
                );
            }
        }
    }

    private String generateUniqueOrderNumber() {
        int maxAttempts = 3;
        int attempt = 0;

        while (attempt < maxAttempts) {
            String orderNumber = orderNumberGenerator.generate();
            if (!orderRepository.existsByOrderNumber(orderNumber)) {
                return orderNumber;
            }
            attempt++;
            log.warn("주문번호가 중복 발생하여 재시도합니다. (시도 횟수: {})", attempt);
        }
        throw new OrderCreationException();
    }

    private void validateSalePostOwner(Long userId, Long salePostId) {
        if (!salePostRepository.existsByIdAndUser_Id(salePostId, userId)) {
            throw new SalePostNotFoundException(salePostId);
        }
    }

    private List<OrderItem> validateAndGetOrderItems(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrder_id(orderId);

        if (orderItems.isEmpty()) {
            throw new OrderItemMissingForOrderException(orderId);
        }

        return orderItems;
    }

    private Map<Long, List<OrderItem>> validateAndGetOrderItemsByOrderId(List<Order> orders) {
        if (orders.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> orderIds = orders.stream()
                .map(Order::getId)
                .toList();

        List<OrderItem> orderItems =
                orderItemRepository.findByOrder_idIn(orderIds);

        Map<Long, List<OrderItem>> matchedOrderItemMap =
                orderItems.stream()
                .collect(Collectors.groupingBy(orderItem -> orderItem.getOrder().getId()));

        orders.forEach(order -> {
            Long orderId = order.getId();

            if (!matchedOrderItemMap.containsKey(orderId)) {
                throw new OrderItemMissingForOrderException(orderId);
            }
        });

        return matchedOrderItemMap;
    }
}

