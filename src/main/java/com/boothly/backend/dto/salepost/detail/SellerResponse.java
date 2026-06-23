package com.boothly.backend.dto.salepost.detail;

import com.boothly.backend.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SellerResponse {
    private final Long id;
    private final String nickname;

    public static SellerResponse from(User user) {
        return SellerResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .build();
    }

    @Builder
    private SellerResponse(Long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }
}
