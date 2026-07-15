package com.boothly.backend.common.exception.salepost;

import com.boothly.backend.common.exception.BusinessException;
import com.boothly.backend.common.exception.ErrorCode;

public class ProductOptionException extends BusinessException {
    public ProductOptionException(ErrorCode errorCode, Long salePostId, Long productOptionId) {
        super(errorCode,
                errorCode.getMessage()
                        + " salePostId=" + salePostId
                        + ", productOptionId=" + productOptionId);
    }
}
