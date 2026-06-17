package com.boothly.backend.common.exception.salepost;

public class InvalidSalePostStateException extends RuntimeException {
    public InvalidSalePostStateException(Long salePostId, String reason) {
      super("salePostId:" + salePostId + ", reason:" + reason);
    }
}
