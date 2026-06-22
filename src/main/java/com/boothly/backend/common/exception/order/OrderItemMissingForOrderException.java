package com.boothly.backend.common.exception.order;

import com.boothly.backend.common.exception.BusinessException;
import com.boothly.backend.common.exception.ErrorCode;

public class OrderItemMissingForOrderException extends BusinessException {
    public OrderItemMissingForOrderException(Long orderId) {
        super(
                ErrorCode.ORDER_ITEM_MISSING_FOR_ORDER,
                " orderId=" + orderId
        );
    }
}
