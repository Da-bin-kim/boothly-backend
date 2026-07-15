package com.boothly.backend.common.exception.salepost;

import com.boothly.backend.common.exception.BusinessException;
import com.boothly.backend.common.exception.ErrorCode;

public class SalePostHasOngoingOrdersException extends BusinessException {
    public SalePostHasOngoingOrdersException(Long salePostId) {
        super(
                ErrorCode.SALE_POST_HAS_ONGOING_ORDERS,
                "처리 중인 주문이 남아 있어 판매글을 숨길 수 없습니다."
                        + " salePostId: " + salePostId
        );
    }
}
