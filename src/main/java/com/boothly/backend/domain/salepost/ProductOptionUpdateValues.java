package com.boothly.backend.domain.salepost;

public record ProductOptionUpdateValues (
        int displayOrder,
        String name,
        String description,
        String imageUrl,
        int price,
        Integer cost,
        int stockQuantity,
        Integer maxQuantityPerOrder,
        boolean isActive,
        boolean isDeleted
){}
