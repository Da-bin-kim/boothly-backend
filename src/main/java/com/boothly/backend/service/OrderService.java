package com.boothly.backend.service;

import com.boothly.backend.common.exception.order.OrderCreationException;
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
import com.boothly.backend.dto.order.OrderCreateRequest;
import com.boothly.backend.dto.order.OrderCreateResponse;
import com.boothly.backend.dto.order.OrderItemCreateRequest;
import com.boothly.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

        SalePost salePost = validateAndGetSalePost(salePostId);
        Map<Long, ProductOption> productOptions = validateAndGetProductOptions(salePostId, request.getOrderItems());
        DeliveryMethod deliveryMethod = validateAndGetDeliveryMethod(salePostId, request.getDeliveryMethodId());
        int itemsTotalPrice = getItemsTotalPrice(productOptions, request.getOrderItems());

        Order order = saveOrder(buyer, salePost, deliveryMethod, request, itemsTotalPrice);
        List<OrderItem> orderItems = saveOrderItems(order, productOptions, request.getOrderItems());

        return OrderCreateResponse.from(order, orderItems);
    }


    private SalePost validateAndGetSalePost(Long salePostId) {
        SalePost salePost = salePostRepository.findById(salePostId).orElseThrow(() ->
                new SalePostNotFoundException(salePostId));
        if (salePost.getSaleStatus() != SalePostStatus.OPEN) {
            throw new SalePostNotOpenException(salePostId);
        }
        return salePost;
    }


    private Map<Long, ProductOption> validateAndGetProductOptions (Long salePostId, List<OrderItemCreateRequest> orderItems) {
        // 1. 중복된 상품 옵션이 있는지 검증
        Set<Long> uniqueOptionIds = orderItems.stream()
                .map(OrderItemCreateRequest::getProductOptionId)
                .collect(Collectors.toSet());

        if(orderItems.size() != uniqueOptionIds.size()) {
            throw new DuplicateProductOptionException();
        }

        // 2. 존재하지 않거나 다른 게시글의 옵션인지 검증
        List<ProductOption> matchedProductOptions = productOptionRepository.findAllBySalePost_IdAndIdIn(salePostId, uniqueOptionIds);

        if (matchedProductOptions.size() != uniqueOptionIds.size()) {
            throw new InvalidSalePostOptionException(salePostId, null);
        }

        // 3. 판매 상태 및 재고 상태, 최대 주문 갯수 검증
        Map<Long, ProductOption> matchedOptionMap = matchedProductOptions.stream()
                .collect(Collectors.toMap(ProductOption::getId, productOption -> productOption));

        for (OrderItemCreateRequest requestedItem : orderItems) {
            ProductOption matchedOption = matchedOptionMap.get(requestedItem.getProductOptionId());

            if (!matchedOption.isActive()) {
                throw new InvalidSalePostOptionException(salePostId, matchedOption.getId());
            }

            if (requestedItem.getQuantity() > matchedOption.getStockQuantity()) {
                throw new InsufficientProductOptionStockException(
                        matchedOption.getId(),
                        requestedItem.getQuantity(),
                        matchedOption.getStockQuantity()
                );
            }
            if (matchedOption.getMaxQuantityPerOrder() != null
                    && (requestedItem.getQuantity() > matchedOption.getMaxQuantityPerOrder())) {
                throw  new ProductOptionQuantityLimitExceededException(
                        matchedOption.getId(),
                        requestedItem.getQuantity(),
                        matchedOption.getMaxQuantityPerOrder()
                );
            }
        }

        return matchedOptionMap;
    }

    private DeliveryMethod validateAndGetDeliveryMethod(Long salePostId, Long deliveryMethodId) {
        DeliveryMethod deliveryMethod = deliveryMethodRepository
                .findBySalePost_IdAndId(salePostId, deliveryMethodId)
                .orElseThrow(()->new DeliveryMethodNotFoundException(salePostId, deliveryMethodId));
        if(!deliveryMethod.isActive()){
            throw new InactiveDeliveryMethodException(salePostId, deliveryMethodId);
        }
        return deliveryMethod;
    }

    private int getItemsTotalPrice(Map<Long, ProductOption> productOptions, List<OrderItemCreateRequest> orderItems) {
        int itemsTotalPrice = 0;
        for(OrderItemCreateRequest request : orderItems) {
            ProductOption productOption = productOptions.get(request.getProductOptionId());
            itemsTotalPrice += productOption.getPrice() * request.getQuantity();
        }
        return itemsTotalPrice;
    }

    private Order saveOrder(User buyer, SalePost salePost, DeliveryMethod deliveryMethod, OrderCreateRequest request, int itemsTotalPrice) {
        Order.OrderBuilder orderBuilder = Order.builder()
                .buyer(buyer)
                .salePost(salePost)
                .payerName(request.getPayerName())
                .orderStatus(OrderStatus.REQUESTED)
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
                .accountHolder(request.getAccountHolder());

        int maxAttempts = 3;
        int attempt = 0;

        while (attempt < maxAttempts) {
            try{
                String orderNumber = orderNumberGenerator.generate();
                Order order = orderBuilder.orderNumber(orderNumber).build();
                return orderRepository.save(order);
            } catch (DataIntegrityViolationException e){
                attempt++;
                log.warn("주문번호가 중복 발생하여 재시도합니다. (시도 횟수: {})", attempt);
            }
        }
        throw new OrderCreationException();
    }

    private List<OrderItem> saveOrderItems(
            Order order,
            Map<Long, ProductOption> productOptions,
            List<OrderItemCreateRequest> orderItemCreateRequests) {

        List<OrderItem> orderItems = new ArrayList<>();

        for(OrderItemCreateRequest request : orderItemCreateRequests) {
            ProductOption productOption = productOptions.get(request.getProductOptionId());
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productOption(productOption)
                    .productOptionName(productOption.getName())
                    .unitPrice(productOption.getPrice())
                    .unitCost(productOption.getCost())
                    .quantity(request.getQuantity())
                    .build();
            orderItems.add(orderItem);

            int updated = productOptionRepository.decreaseStock(
                    order.getSalePost().getId(),
                    productOption.getId(),
                    request.getQuantity()
            );
            if(updated == 0){
                throw new InsufficientProductOptionStockException(
                        request.getProductOptionId(),
                        request.getQuantity(),
                        0
                );
            }
        }

        return orderItemRepository.saveAll(orderItems);
    }
}

