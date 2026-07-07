package com.boothly.backend.dto.salepost.create;

import com.boothly.backend.domain.enums.DeliveryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class DeliveryMethodCreateRequest {
    @NotBlank
    @Size(max = 50)
    private String methodName;

    @NotNull
    private DeliveryType deliveryType;

    @PositiveOrZero
    private int deliveryFee;
}
