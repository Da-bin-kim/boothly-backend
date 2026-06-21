package com.boothly.backend.dto.order.detail;

import com.boothly.backend.domain.salepost.SalePost;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderSalePostDetailResponse {
    private Long id;
    private String title;
    private String thumbnailImageUrl;
    private String sellerName;

    public static OrderSalePostDetailResponse from(SalePost salePost) {
        return OrderSalePostDetailResponse.builder()
                .id(salePost.getId())
                .title(salePost.getTitle())
                .thumbnailImageUrl(salePost.getThumbnailImageUrl())
                .sellerName(salePost.getUser().getName())
                .build();
    }
}
