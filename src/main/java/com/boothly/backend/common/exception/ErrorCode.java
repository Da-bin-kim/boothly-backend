package com.boothly.backend.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INVALID_REQUEST(
            HttpStatus.BAD_REQUEST,
            "INVALID_REQUEST",
            "잘못된 요청입니다."
    ),
    SALE_POST_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "SALE_POST_NOT_FOUND",
            "판매글을 찾을 수 없습니다."
    ),
    SALE_POST_INVALID_STATE(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "SALE_POST_INVALID_STATE",
            "판매글 정보를 불러오는 중 오류가 발생했습니다."
    ),
    INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_SERVER_ERROR",
            "서버 내부 오류가 발생했습니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}