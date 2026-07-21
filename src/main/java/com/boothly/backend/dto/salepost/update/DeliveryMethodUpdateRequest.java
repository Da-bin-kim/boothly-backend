package com.boothly.backend.dto.salepost.update;

import com.boothly.backend.domain.enums.DeliveryType;
import com.boothly.backend.domain.salepost.DeliveryMethodUpdateValues;
import com.boothly.backend.dto.salepost.create.DeliveryMethodCreateRequest;
import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class DeliveryMethodUpdateRequest {
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String methodName;

    @NotNull
    private DeliveryType deliveryType;

    @NotNull
    @PositiveOrZero
    private Integer deliveryFee;

    @NotNull
    private Boolean isActive;

    @NotNull
    private Boolean isDeleted;

    @AssertTrue(message = "신규 배송 방법은 활성화 상태이고 삭제되지 않은 상태여야 합니다.")
    public boolean isValidCreateState() {
        if (!isCreateRequest()) {
            return true;
        }

        return Boolean.TRUE.equals(isActive)
                && Boolean.FALSE.equals(isDeleted);
    }

    public DeliveryMethodUpdateValues toUpdateValues() {
        return new DeliveryMethodUpdateValues(
                methodName,
                deliveryType,
                deliveryFee,
                isActive,
                isDeleted
        );
    }

    public DeliveryMethodCreateRequest toCreateRequest() {
        return DeliveryMethodCreateRequest.builder()
                .methodName(methodName)
                .deliveryType(deliveryType)
                .deliveryFee(deliveryFee)
                .build();
    }

    public boolean isCreateRequest() {
        return id == null;
    }
}
