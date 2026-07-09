package com.boothly.backend.common.exception.salepost;

import com.boothly.backend.common.exception.BusinessException;
import com.boothly.backend.common.exception.ErrorCode;

public class DuplicateDeliveryMethodIdException extends BusinessException {
    public DuplicateDeliveryMethodIdException() {
        super(
                ErrorCode.DUPLICATE_DELIVERY_METHOD_ID
        );
    }
}
