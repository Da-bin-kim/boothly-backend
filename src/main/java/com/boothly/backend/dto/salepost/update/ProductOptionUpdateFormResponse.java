package com.boothly.backend.dto.salepost.update;

import com.boothly.backend.domain.salepost.ProductOption;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductOptionUpdateFormResponse {
    private final Long id;
    private final int displayOrder;
    private final String name;
    private final String description;
    private final String imageUrl;
    private final int price;
    private final Integer cost;
    private final int stockQuantity;
    private final Integer maxQuantityPerOrder;

    public static ProductOptionUpdateFormResponse from(ProductOption productOption) {
        return ProductOptionUpdateFormResponse.builder()
                .id(productOption.getId())
                .displayOrder(productOption.getDisplayOrder())
                .name(productOption.getName())
                .description(productOption.getDescription())
                .imageUrl(productOption.getImageUrl())
                .price(productOption.getPrice())
                .cost(productOption.getCost())
                .stockQuantity(productOption.getStockQuantity())
                .maxQuantityPerOrder(productOption.getMaxQuantityPerOrder())
                .build();
    }
}
