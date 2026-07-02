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
    private Long orderId;
    private String orderNumber;
    private OrderSalePostResponse salePost;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static OrderListResponse from(Order order) {
        return OrderListResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .salePost(OrderSalePostResponse.from(order.getSalePost()))
                .orderStatus(order.getOrderStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
