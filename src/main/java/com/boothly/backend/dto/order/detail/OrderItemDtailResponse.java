package com.boothly.backend.dto.order.detail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OrderItemDtailResponse {
    private String productOptionName;
    private int unitPrice;
    private int quantity;
}
