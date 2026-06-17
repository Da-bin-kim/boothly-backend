package com.boothly.backend.common.exception.salepost;

import com.boothly.backend.common.exception.BusinessException;
import com.boothly.backend.common.exception.ErrorCode;

public class SalePostNotFoundException extends BusinessException {
    public SalePostNotFoundException(Long salePostId) {
        super(ErrorCode.SALE_POST_NOT_FOUND,
                "판매글을 찾을 수 없습니다."
                        + " salePostId=" + salePostId
        );
    }
}
