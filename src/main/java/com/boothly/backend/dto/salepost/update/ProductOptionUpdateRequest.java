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

    public ProductOptionUpdateValues toUpdateValues() {
        return new ProductOptionUpdateValues(
                displayOrder,
                name,
                description, //수정가능
                imageUrl, //수정가능
                price,
                cost,
                stockQuantity,//수정가능
                maxQuantityPerOrder
        );
    }
}
