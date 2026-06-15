package com.boothly.backend.dto.order;

import com.boothly.backend.domain.enums.DeliveryType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FulfillmentInfoResponse {
    private final String recipientName;
    private final String recipientPhone;
    private final DeliveryType deliveryType;
    private final String deliveryMethodName;
    private final String deliveryAddress;
}
