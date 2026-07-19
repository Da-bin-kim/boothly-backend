package com.boothly.backend.domain.salepost;

import com.boothly.backend.domain.enums.DeliveryType;

public record DeliveryMethodUpdateValues(
        String methodName,
        DeliveryType deliveryType,
        int deliveryFee,
        boolean isActive,
        boolean isDeleted
) {
}
