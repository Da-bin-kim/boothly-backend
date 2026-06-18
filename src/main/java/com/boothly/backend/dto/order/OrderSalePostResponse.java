package com.boothly.backend.dto.order;

import com.boothly.backend.domain.salepost.SalePost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OrderSalePostResponse {
    private Long salePostId;
    private String title;
    private String thumbnailImageUrl;

    public static OrderSalePostResponse from(SalePost salePost) {
        return OrderSalePostResponse.builder()
                .salePostId(salePost.getId())
                .title(salePost.getTitle())
                .thumbnailImageUrl(salePost.getThumbnailImageUrl())
                .build();
    }
}
