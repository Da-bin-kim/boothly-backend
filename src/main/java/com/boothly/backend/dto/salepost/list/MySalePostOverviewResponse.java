package com.boothly.backend.dto.salepost.list;

import com.boothly.backend.domain.enums.SalePostStatus;
import com.boothly.backend.domain.salepost.SalePost;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class MySalePostOverviewResponse {
    private Long salePostId;
    private String title;
    private SalePostStatus saleStatus;
    private String thumbnailImageUrl;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime createdAt;

    public static MySalePostOverviewResponse from(SalePost salePost) {
        return MySalePostOverviewResponse.builder()
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
