package com.boothly.backend.dto.salepost.list;

import com.boothly.backend.domain.enums.SalePostStatus;
import com.boothly.backend.domain.salepost.SalePost;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SalePostListResponse {
    private Long id;
    private Long sellerId;
    private String sellerNickname;
    private String title;
    private SalePostStatus saleStatus;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String thumbnailImageUrl;
    private LocalDateTime createdAt;

    public static SalePostListResponse from(SalePost salePost) {
        return SalePostListResponse.builder()
                .id(salePost.getId())
                .sellerId(salePost.getUser().getId())
                .sellerNickname(salePost.getUser().getNickname())
                .title(salePost.getTitle())
                .saleStatus(salePost.getSaleStatus())
                .startAt(salePost.getStartAt())
                .endAt(salePost.getEndAt())
                .thumbnailImageUrl(salePost.getThumbnailImageUrl())
                .createdAt(salePost.getCreatedAt())
                .build();
    }

    @Builder
    public SalePostListResponse(Long id, Long sellerId, String sellerNickname, String title, SalePostStatus saleStatus, LocalDateTime startAt, LocalDateTime endAt, String thumbnailImageUrl, LocalDateTime createdAt) {
        this.id = id;
        this.sellerId = sellerId;
        this.sellerNickname = sellerNickname;
        this.title = title;
        this.saleStatus = saleStatus;
        this.startAt = startAt;
        this.endAt = endAt;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.createdAt = createdAt;
    }
}
