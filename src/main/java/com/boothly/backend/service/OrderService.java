package com.boothly.backend.service;

import com.boothly.backend.common.exception.*;
import com.boothly.backend.domain.enums.SalePostStatus;
import com.boothly.backend.domain.salepost.ProductOption;
import com.boothly.backend.domain.salepost.SalePost;
import com.boothly.backend.domain.user.User;
import com.boothly.backend.dto.order.OrderCreateRequest;
import com.boothly.backend.dto.order.OrderCreateResponse;
import com.boothly.backend.dto.order.OrderItemCreateRequest;
import com.boothly.backend.repository.OrderRepository;
import com.boothly.backend.repository.ProductOptionRepository;
import com.boothly.backend.repository.SalePostRepository;
import com.boothly.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final SalePostRepository salePostRepository;
    private final ProductOptionRepository productOptionRepository;

    @Transactional
    public OrderCreateResponse createOrder(Long buyerId, Long salePostId, OrderCreateRequest request) {
        User buyer = userRepository.findById(buyerId).orElseThrow(() ->
                new UserNotFoundException(buyerId));

        SalePost salePost = validateAndGetSalePost(salePostId);
        Map<Long, ProductOption> matchedOptionMap = matchedOptionMap(salePostId, request.getOrderItems());


        //금액 계산, 재고 차감, 주문 당시 단가 저장


        throw new UnsupportedOperationException(
                "Order 생성 및 응답 변환은 아직 구현되지 않았습니다."
        );
    }

    /**
     * 사용자가 요청한 게시글이 존재하는지 검증하고,
     * 조회가 성공하면 게시글 객체를 반환한다.
     * @param salePostId
     * @return SalePost
     */
    private SalePost validateAndGetSalePost(Long salePostId) {
        SalePost salePost = salePostRepository.findById(salePostId).orElseThrow(() ->
                new SalePostNotFoundException(salePostId));
        if (salePost.getSaleStatus() != SalePostStatus.OPEN) {
            throw new SalePostNotOpenException(salePostId);
        }
        return salePost;
    }


    private Map<Long, ProductOption> matchedOptionMap (Long salePostId, List<OrderItemCreateRequest> orderItems) {
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

        // 3. 판매 상태 및 재고 상태 검증
        // 빠른 탐색을 위한 MAP 변환
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
        }

        return matchedOptionMap;
    }
}

