package com.boothly.backend.common.exception.order;

import com.boothly.backend.common.exception.BusinessException;
import com.boothly.backend.common.exception.ErrorCode;

public class OrderNotFoundException extends BusinessException {
    public OrderNotFoundException(long userId, long orderId) {
        super(ErrorCode.ORDER_NOT_FOUND,
                " userId=" + userId +
                " orderId=" + orderId
        );
    }

    public OrderNotFoundException(long salePostId, long sellerId, long orderId) {
        super(ErrorCode.ORDER_NOT_FOUND,
                " sellerId=" + sellerId +
                " salePostId=" + salePostId +
                " orderId=" + orderId
        );
    }
}
