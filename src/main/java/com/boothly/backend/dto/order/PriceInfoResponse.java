package com.boothly.backend.dto.order;

import com.boothly.backend.domain.order.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PriceInfoResponse {
    private final int deliveryFee;
    private final int itemsTotalPrice;

    public static PriceInfoResponse from(Order order) {
        return PriceInfoResponse.builder()
                .deliveryFee(order.getDeliveryFee())
                .itemsTotalPrice(order.getItemsTotalPrice())
                .build();
    }
}
