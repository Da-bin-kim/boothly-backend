package com.boothly.backend.dto.order;

import com.boothly.backend.domain.order.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OrderItemCreateResponse {
    private final long id;
    private final long productOptionId;
    private final String productOptionName;
    private final int unitPrice;
    private final int quantity;

    public static OrderItemCreateResponse from(OrderItem orderItem) {
        return OrderItemCreateResponse.builder()
                .id(orderItem.getId())
                .productOptionId(orderItem.getProductOption().getId())
                .productOptionName(orderItem.getProductOptionName())
                .unitPrice(orderItem.getUnitPrice())
                .quantity(orderItem.getQuantity())
                .build();
    }
}
