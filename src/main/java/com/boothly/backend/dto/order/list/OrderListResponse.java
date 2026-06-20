package com.boothly.backend.dto.order.list;

import com.boothly.backend.domain.enums.OrderStatus;
import com.boothly.backend.domain.order.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class OrderListResponse {
    private Long id;
    private String orderNumber;
    private OrderSalePostResponse orderSalePostResponse;
    private OrderStatus status;
    private LocalDateTime createdAt;

    public static OrderListResponse from(Order order) {
        return OrderListResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderSalePostResponse(OrderSalePostResponse.from(order.getSalePost()))
                .status(order.getOrderStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
