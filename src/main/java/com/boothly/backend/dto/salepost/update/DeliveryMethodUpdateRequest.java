package com.boothly.backend.dto.salepost.update;

import com.boothly.backend.domain.enums.DeliveryType;
import com.boothly.backend.domain.salepost.DeliveryMethodUpdateValues;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class DeliveryMethodUpdateRequest {
    @NotNull
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String methodName;

    @NotNull
    private DeliveryType deliveryType;

    @NotNull
    @PositiveOrZero
    private Integer deliveryFee;

    public DeliveryMethodUpdateValues toUpdateValues() {
        return new DeliveryMethodUpdateValues(
                methodName,
                deliveryType,
                deliveryFee
        );
    }
}
