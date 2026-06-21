package com.boothly.backend.dto.order.detail;

import com.boothly.backend.domain.order.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OrderItemDetailResponse {
    private String productOptionName;
    private int unitPrice;
    private int quantity;

    public static OrderItemDetailResponse from(OrderItem orderItem) {
        return OrderItemDetailResponse.builder()
                .productOptionName(orderItem.getProductOptionName())
                .unitPrice(orderItem.getUnitPrice())
                .quantity(orderItem.getQuantity())
                .build();
    }
}
