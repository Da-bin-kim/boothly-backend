package com.boothly.backend.common.exception;

public class ProductOptionNotFoundException extends BusinessException {
    public ProductOptionNotFoundException(Long salePostId, Long productOptionId) {
        super(ErrorCode.PRODUCT_OPTION_NOT_FOUND
                , "상품 옵션을 찾을 수 없습니다."
                + " salePostId=" + salePostId
                + ", productOptionId=" + productOptionId);
    }
}
