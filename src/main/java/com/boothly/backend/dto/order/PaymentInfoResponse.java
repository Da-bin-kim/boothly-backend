package com.boothly.backend.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentInfoResponse {
    private final String payerName;
    private final String bankName;
    private final String accountNumber;
    private final String accountHolder;
}
