package com.boothly.backend.common.exception.salepost;

import com.boothly.backend.common.exception.BusinessException;
import com.boothly.backend.common.exception.ErrorCode;

public class ProductOptionQuantityLimitExceededException extends BusinessException {
    public ProductOptionQuantityLimitExceededException(long productOptionId, int requestedQuantity, int availableMaxQuantity) {
      super(ErrorCode.MAX_ORDER_QUANTITY_EXCEEDED
              ,"최대 주문 가능 수량을 초과했습니다."
                      + " productOptionId=" + productOptionId
                      + ", requestedQuantity=" + requestedQuantity
                      + ", availableMaxQuantity=" + availableMaxQuantity);
    }
}
