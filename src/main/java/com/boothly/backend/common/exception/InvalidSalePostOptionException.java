package com.boothly.backend.common.exception;

public class InvalidSalePostOptionException extends BusinessException {
    public InvalidSalePostOptionException(Long salePostId, Long optionId) {
        super(ErrorCode.INVALID_SALE_POST_OPTION
                ,"해당 게시글에서 주문할 수 없는 옵션입니다."
                + " salePostId=" + salePostId
                + " optionId=" + optionId
        );
    }
}
