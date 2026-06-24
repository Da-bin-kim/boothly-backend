package com.boothly.backend.common.exception.salepost;

import com.boothly.backend.common.exception.BusinessException;
import com.boothly.backend.common.exception.ErrorCode;
import com.boothly.backend.domain.enums.SalePostStatus;

public class InvalidSaleStatusTransitionException extends BusinessException {
    public InvalidSaleStatusTransitionException(SalePostStatus currentStatus, SalePostStatus targetStatus) {
        super(
                ErrorCode.INVALID_SALE_STATUS_TRANSITION
                , " currentStatus = " + currentStatus
                + " targetStatus =" + targetStatus
        );
    }
}
