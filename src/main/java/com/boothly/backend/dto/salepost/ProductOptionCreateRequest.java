package com.boothly.backend.dto.salepost;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ProductOptionCreateRequest {
    private boolean active;

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
