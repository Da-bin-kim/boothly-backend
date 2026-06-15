package com.boothly.backend.common.exception;

public class DuplicateProductOptionException extends BusinessException {
    public DuplicateProductOptionException() {
        super(
                ErrorCode.PRODUCT_OPTION_ALREADY_EXISTS
        );
    }
}
