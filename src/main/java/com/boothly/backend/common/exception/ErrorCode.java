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
    USER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "USER_NOT_FOUND",
            "사용자를 찾을 수 없습니다."
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
    ),
    SALE_POST_NOT_OPEN(
            HttpStatus.CONFLICT,
            "SALE_POST_NOT_OPEN",
            "현재 판매 중인 게시글이 아닙니다."
    ),
    INVALID_SALE_POST_OPTION(
            HttpStatus.CONFLICT,
            "INVALID_SALE_POST_OPTION",
            "해당 판매글에서 선택할 수 없는 상품 옵션입니다."
    ),
    PRODUCT_OPTION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PRODUCT_OPTION_NOT_FOUND",
            "판매 옵션을 찾을 수 없습니다."
    ),
    INSUFFICIENT_PRODUCT_OPTION_STOCK(
            HttpStatus.CONFLICT,
            "INSUFFICIENT_PRODUCT_OPTION_STOCK",
            "선택한 상품 옵션의 재고가 부족합니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}