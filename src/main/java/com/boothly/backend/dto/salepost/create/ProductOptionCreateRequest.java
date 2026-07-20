package com.boothly.backend.dto.salepost.create;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProductOptionCreateRequest {
    @PositiveOrZero
    private int displayOrder;

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 255)
    private String description;

    @Size(max = 500)
    private String imageUrl;

    @PositiveOrZero
    private int price;

    @PositiveOrZero
    private Integer cost;

    @PositiveOrZero
    private int stockQuantity;

    @Positive
    private Integer maxQuantityPerOrder;
}
