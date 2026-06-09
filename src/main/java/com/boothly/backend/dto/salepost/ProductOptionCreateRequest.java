package com.boothly.backend.dto.salepost;

import lombok.Getter;

@Getter
public class ProductOptionCreateRequest {
    private boolean active;
    private int displayOrder;
    private String name;
    private String description;
    private String imageUrl;
    private int price;
    private Integer cost;
    private int stockQuantity;
    private Integer maxQuantityPerOrder;
}
