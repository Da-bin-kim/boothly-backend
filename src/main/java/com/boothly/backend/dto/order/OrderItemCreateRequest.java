package com.boothly.backend.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OrderItemCreateRequest {
    @NotNull
    @Positive
    private final Long productOptionId;

    @Positive
    private final int quantity;
}
