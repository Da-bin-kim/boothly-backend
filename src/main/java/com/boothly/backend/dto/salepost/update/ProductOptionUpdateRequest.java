package com.boothly.backend.dto.salepost.update;

import com.boothly.backend.domain.salepost.ProductOptionUpdateValues;
import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class ProductOptionUpdateRequest {
    @NotNull
    private Long id;

    @NotNull
    @PositiveOrZero
    private Integer displayOrder;

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 255)
    private String description;

    @Size(max = 500)
    private String imageUrl;

    @NotNull
    @PositiveOrZero
    private Integer price;

    @PositiveOrZero
    private Integer cost;

    @NotNull
    @PositiveOrZero
    private Integer stockQuantity;

    @Positive
    private Integer maxQuantityPerOrder;

    @NotNull
    private Boolean isActive;

    @NotNull
    private Boolean isDeleted;

    public ProductOptionUpdateValues toUpdateValues() {
        return new ProductOptionUpdateValues(
                displayOrder,
                name,
                description,
                imageUrl,
                price,
                cost,
                stockQuantity,
                maxQuantityPerOrder,
                isActive,
                isDeleted
        );
    }
}
