package com.boothly.backend.dto.order;

import com.boothly.backend.domain.enums.DeliveryType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderItemCreateResponse {
    private final int id;
    private final int productOptionId;
    private final String productOptionName;
    private final int unitPrice;
    private final int quantity;
}
