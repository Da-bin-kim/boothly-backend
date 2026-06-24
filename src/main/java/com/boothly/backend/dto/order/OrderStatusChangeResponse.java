package com.boothly.backend.dto.order;

import com.boothly.backend.domain.enums.OrderStatus;
import com.boothly.backend.domain.order.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
public class OrderStatusChangeResponse {
    private final Long orderId;
    private final OrderStatus orderStatus;
    private final LocalDateTime updatedAt;

    public static OrderStatusChangeResponse from(Order order) {
        return OrderStatusChangeResponse.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
