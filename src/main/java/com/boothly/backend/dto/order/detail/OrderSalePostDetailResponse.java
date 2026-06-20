package com.boothly.backend.dto.order.detail;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderSalePostDetailResponse {
    private Long id;
    private String title;
    private String thumbnailImageUrl;
    private String sellerName;
}
