package com.boothly.backend.dto.salepost;

import com.boothly.backend.domain.enums.DeliveryType;
import lombok.Getter;

@Getter
public class DeliveryMethodCreateRequest {
    private String methodName;
    private DeliveryType deliveryType;
    private int deliveryFee;
    private boolean active;
}
