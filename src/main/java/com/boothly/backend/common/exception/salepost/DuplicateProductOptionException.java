package com.boothly.backend.common.exception.salepost;

import com.boothly.backend.common.exception.BusinessException;
import com.boothly.backend.common.exception.ErrorCode;

public class DuplicateProductOptionException extends BusinessException {
    public DuplicateProductOptionException() {
        super(
                ErrorCode.PRODUCT_OPTION_ALREADY_EXISTS
        );
    }
}
