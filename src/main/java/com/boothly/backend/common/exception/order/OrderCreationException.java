package com.boothly.backend.common.exception.order;

import com.boothly.backend.common.exception.BusinessException;
import com.boothly.backend.common.exception.ErrorCode;

public class OrderCreationException extends BusinessException {
    public OrderCreationException() {
        super(ErrorCode.ORDER_NUMBER_DUPLICATED
                ,"주문 생성에 최종 실패했습니다.");
    }
}
