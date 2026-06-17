package com.boothly.backend.common.exception.salepost;

import com.boothly.backend.common.exception.BusinessException;
import com.boothly.backend.common.exception.ErrorCode;

public class SalePostNotOpenException extends BusinessException {
    public SalePostNotOpenException(Long salePostId) {
      super(ErrorCode.SALE_POST_NOT_OPEN
              , "판매 중인 게시글이 아닙니다."
              + " salePostId=" + salePostId);
    }
}
