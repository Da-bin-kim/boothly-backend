package com.boothly.backend.common.exception;

public class InsufficientProductOptionStockException extends BusinessException {
  public InsufficientProductOptionStockException(Long productOptionId, int requestedQuantity, int availableQuantity) {
    super(ErrorCode.INSUFFICIENT_PRODUCT_OPTION_STOCK
            , "상품 옵션의 재고가 부족합니다."
            + " productOptionId=" + productOptionId
            + ", requestedQuantity=" + requestedQuantity
            + ", availableQuantity=" + availableQuantity);
  }
}
