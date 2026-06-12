package com.boothly.backend.common.exception;

public class InvalidSalePostOptionException extends BusinessException {
    public InvalidSalePostOptionException(Long salePostId, Long productOptionId) {
        super(ErrorCode.INVALID_SALE_POST_OPTION
                ,"상품 옵션이 해당 판매글에 속하지 않습니다."
                + " salePostId=" + salePostId
                + ", productOptionId=" + productOptionId
        );
    }
}
