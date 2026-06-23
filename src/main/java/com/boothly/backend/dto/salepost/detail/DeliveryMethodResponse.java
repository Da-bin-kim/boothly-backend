package com.boothly.backend.dto.salepost.detail;

import com.boothly.backend.domain.enums.DeliveryType;
import com.boothly.backend.domain.salepost.DeliveryMethod;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DeliveryMethodResponse {
    private final Long id;
    private final String methodName;
    private final DeliveryType deliveryType;
    private final int deliveryFee;
    private final boolean active;

    public static DeliveryMethodResponse from(DeliveryMethod method) {
        return DeliveryMethodResponse.builder()
                .id(method.getId())
                .methodName(method.getMethodName())
                .deliveryType(method.getDeliveryType())
                .deliveryFee(method.getDeliveryFee())
                .active(method.isActive())
                .build();
    }

    @Builder
    private DeliveryMethodResponse(Long id, String methodName, DeliveryType deliveryType, int deliveryFee, boolean active) {
        this.id = id;
        this.methodName = methodName;
        this.deliveryType = deliveryType;
        this.deliveryFee = deliveryFee;
        this.active = active;
    }
}
