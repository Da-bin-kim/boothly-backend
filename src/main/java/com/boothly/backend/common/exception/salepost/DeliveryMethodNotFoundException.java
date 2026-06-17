package com.boothly.backend.common.exception.salepost;

import com.boothly.backend.common.exception.BusinessException;
import com.boothly.backend.common.exception.ErrorCode;

public class DeliveryMethodNotFoundException extends BusinessException {
    public DeliveryMethodNotFoundException(Long salePostId, Long deliveryMethodId) {
        super(ErrorCode.DELIVERY_METHOD_NOT_FOUND
                ,"일치하는 배송 옵션을 찾을 수 없습니다."
                + " salePostId=" + salePostId
                + "Delivery Method " + deliveryMethodId);
    }
}
