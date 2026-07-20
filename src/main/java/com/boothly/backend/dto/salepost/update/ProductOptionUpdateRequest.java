package com.boothly.backend.dto.salepost.update;

import com.boothly.backend.domain.salepost.ProductOptionUpdateValues;
import com.boothly.backend.dto.salepost.create.ProductOptionCreateRequest;
import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class ProductOptionUpdateRequest {
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

    @AssertTrue(message = "신규 상품 옵션은 활성화 상태이고 삭제되지 않은 상태여야 합니다.")
    public boolean isValidCreateState() {
        if (!isCreateRequest()) {
            return true;
        }

        return Boolean.TRUE.equals(isActive)
                && Boolean.FALSE.equals(isDeleted);
    }

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

    public ProductOptionCreateRequest toCreateRequest() {
        return ProductOptionCreateRequest.builder()
                .displayOrder(displayOrder)
                .name(name)
                .description(description)
                .imageUrl(imageUrl)
                .price(price)
                .cost(cost)
                .stockQuantity(stockQuantity)
                .maxQuantityPerOrder(maxQuantityPerOrder)
                .build();
    }

    public boolean isCreateRequest() {
        return id == null;
    }
}
