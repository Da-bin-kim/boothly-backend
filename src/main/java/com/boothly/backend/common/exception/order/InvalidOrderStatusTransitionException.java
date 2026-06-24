package com.boothly.backend.common.exception.order;

import com.boothly.backend.common.exception.BusinessException;
import com.boothly.backend.common.exception.ErrorCode;
import com.boothly.backend.domain.enums.OrderStatus;

public class InvalidOrderStatusTransitionException extends BusinessException {
    public InvalidOrderStatusTransitionException(OrderStatus currentOrderStatus, OrderStatus targetOrderStatus) {
        super(
                ErrorCode.INVALID_ORDER_STATUS_TRANSITION,
                ErrorCode.INVALID_ORDER_STATUS_TRANSITION.getMessage() +
                " currentOrderStatus = " + currentOrderStatus +
                ", targetOrderStatus = " + targetOrderStatus
        );
    }
}
