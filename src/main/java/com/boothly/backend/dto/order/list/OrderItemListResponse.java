package com.boothly.backend.dto.order.list;

import com.boothly.backend.domain.order.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OrderItemListResponse {
    private String productOptionName;
    private int quantity;

    public static OrderItemListResponse from(OrderItem orderItem) {
        return OrderItemListResponse.builder()
                .productOptionName(orderItem.getProductOptionName())
                .quantity(orderItem.getQuantity())
                .build();
    }
}
