package com.boothly.backend.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PriceInfoResponse {
    private final int deliveryFee;
    private final int itemsTotalPrice;
    private final int totalPrice;
}
