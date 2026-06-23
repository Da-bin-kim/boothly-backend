package com.boothly.backend.dto.salepost;

import com.boothly.backend.domain.enums.SalePostStatus;
import com.boothly.backend.domain.salepost.SalePost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SalePostCloseResponse {
    private Long salePostId;
    private SalePostStatus status;

    public static SalePostCloseResponse from(SalePost salePost) {
        return SalePostCloseResponse.builder()
                .salePostId(salePost.getId())
                .status(salePost.getSaleStatus())
                .build();
    }
}
