package com.boothly.backend.common.exception;

public class SalePostNotFoundException extends RuntimeException{
    public SalePostNotFoundException(Long salePostId) {
        super("salePostId:" + salePostId + ", 판매글을 찾을 수 없습니다.");
    }
}
