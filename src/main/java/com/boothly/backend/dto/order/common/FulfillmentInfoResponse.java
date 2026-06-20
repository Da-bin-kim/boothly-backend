package com.boothly.backend.dto.order.common;

import com.boothly.backend.domain.enums.DeliveryType;
import com.boothly.backend.domain.order.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FulfillmentInfoResponse {
    private final String recipientName;
    private final String recipientPhone;
    private final DeliveryType deliveryType;
    private final String deliveryMethodName;
    private final String deliveryAddress;

    public static FulfillmentInfoResponse from(Order order) {
        return FulfillmentInfoResponse.builder()
                .recipientName(order.getRecipientName())
                .recipientPhone(order.getRecipientPhone())
                .deliveryType(order.getDeliveryType())
                .deliveryMethodName(order.getDeliveryMethodName())
                .deliveryAddress(order.getDeliveryAddress())
                .build();
    }
}
