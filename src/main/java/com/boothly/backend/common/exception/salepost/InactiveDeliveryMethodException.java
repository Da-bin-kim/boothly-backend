package com.boothly.backend.common.exception.salepost;

import com.boothly.backend.common.exception.BusinessException;
import com.boothly.backend.common.exception.ErrorCode;

public class InactiveDeliveryMethodException extends BusinessException {
    public InactiveDeliveryMethodException(long salePostId, Long deliveryMethodId) {
        super(ErrorCode.DELIVERY_METHOD_NOT_FOUND
                ,"배송 옵션이 활성화되지 않았습니다."
                        + " salePostId=" + salePostId
                        + " Delivery Method=" + deliveryMethodId);
    }
}
