package com.boothly.backend.common.exception.salepost;

import com.boothly.backend.common.exception.BusinessException;
import com.boothly.backend.common.exception.ErrorCode;

public class DeliveryMethodException extends BusinessException {
    public DeliveryMethodException(
            ErrorCode errorCode,
            Long salePostId,
            Long DeliveryMethodId) {
        super(errorCode,
                errorCode.getMessage()
                        + " salePostId=" + salePostId
                    + " DeliveryMethodId=" + DeliveryMethodId
        );
    }
}
