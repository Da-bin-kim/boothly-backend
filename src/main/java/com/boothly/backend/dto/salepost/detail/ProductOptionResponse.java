package com.boothly.backend.dto.salepost.detail;

import com.boothly.backend.domain.salepost.ProductOption;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductOptionResponse {
    private final Long id;
    private final boolean active;
    private final int displayOrder;
    private final String name;
    private final String description;
    private final String imageUrl;
    private final int price;
    private final int stockQuantity;
    private final Integer maxQuantityPerOrder;

    public static ProductOptionResponse from(ProductOption option) {
        return ProductOptionResponse.builder()
                .id(option.getId())
                .active(option.isActive())
                .displayOrder(option.getDisplayOrder())
                .name(option.getName())
                .description(option.getDescription())
                .imageUrl(option.getImageUrl())
                .price(option.getPrice())
                .stockQuantity(option.getStockQuantity())
                .maxQuantityPerOrder(option.getMaxQuantityPerOrder())
                .build();
    }

    @Builder
    private ProductOptionResponse(Long id, boolean active, int displayOrder, String name, String description, String imageUrl, int price, int stockQuantity, Integer maxQuantityPerOrder) {
        this.id = id;
        this.active = active;
        this.displayOrder = displayOrder;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.maxQuantityPerOrder = maxQuantityPerOrder;
    }
}
