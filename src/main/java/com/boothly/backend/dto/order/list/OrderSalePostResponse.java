package com.boothly.backend.dto.order.list;

import com.boothly.backend.domain.salepost.SalePost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OrderSalePostResponse {
    private Long id;
    private String title;
    private String thumbnailImageUrl;

    public static OrderSalePostResponse from(SalePost salePost) {
        return OrderSalePostResponse.builder()
                .id(salePost.getId())
                .title(salePost.getTitle())
                .thumbnailImageUrl(salePost.getThumbnailImageUrl())
                .build();
    }
}
