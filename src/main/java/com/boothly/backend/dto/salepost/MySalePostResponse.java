package com.boothly.backend.dto.salepost;

import com.boothly.backend.domain.enums.SalePostStatus;
import com.boothly.backend.domain.salepost.SalePost;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class MySalePostResponse {
    private Long salePostId;
    private String title;
    private SalePostStatus saleStatus;
    private String thumbnailImageUrl;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime createdAt;

    public static MySalePostResponse from(SalePost salePost) {
        return MySalePostResponse.builder()
                .salePostId(salePost.getId())
                .title(salePost.getTitle())
                .saleStatus(salePost.getSaleStatus())
                .thumbnailImageUrl(salePost.getThumbnailImageUrl())
                .startAt(salePost.getStartAt())
                .endAt(salePost.getEndAt())
                .createdAt(salePost.getCreatedAt())
                .build();
    }
}
