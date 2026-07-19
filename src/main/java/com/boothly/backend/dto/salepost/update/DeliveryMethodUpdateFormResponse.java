package com.boothly.backend.dto.salepost.update;

import com.boothly.backend.domain.enums.DeliveryType;
import com.boothly.backend.domain.salepost.DeliveryMethod;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeliveryMethodUpdateFormResponse {
    private final Long id;
    private final String methodName;
    private final DeliveryType deliveryType;
    private final int deliveryFee;
    private final boolean isActive;

    public static DeliveryMethodUpdateFormResponse from(DeliveryMethod deliveryMethod) {
        return DeliveryMethodUpdateFormResponse.builder()
                .id(deliveryMethod.getId())
                .methodName(deliveryMethod.getMethodName())
                .deliveryType(deliveryMethod.getDeliveryType())
                .deliveryFee(deliveryMethod.getDeliveryFee())
                .isActive(deliveryMethod.isActive())
                .build();
    };
}
