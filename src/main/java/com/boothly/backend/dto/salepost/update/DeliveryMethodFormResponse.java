package com.boothly.backend.dto.salepost.update;

import com.boothly.backend.domain.enums.DeliveryType;
import com.boothly.backend.domain.salepost.DeliveryMethod;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeliveryMethodFormResponse {
    private final Long id;
    private final String methodName;
    private final DeliveryType deliveryType;
    private final int deliveryFee;

    public static DeliveryMethodFormResponse from(DeliveryMethod deliveryMethod) {
        return DeliveryMethodFormResponse.builder()
                .id(deliveryMethod.getId())
                .methodName(deliveryMethod.getMethodName())
                .deliveryType(deliveryMethod.getDeliveryType())
                .deliveryFee(deliveryMethod.getDeliveryFee())
                .build();
    };
}
