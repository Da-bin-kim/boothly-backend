package com.boothly.backend.dto.order;

import com.boothly.backend.domain.order.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PaymentInfoResponse {
    private final String payerName;
    private final String bankName;
    private final String accountNumber;
    private final String accountHolder;

    public static PaymentInfoResponse from(Order order) {
        return PaymentInfoResponse.builder()
                .payerName(order.getPayerName())
                .bankName(order.getBankName())
                .accountNumber(order.getAccountNumber())
                .accountHolder(order.getAccountHolder())
                .build();
    }
}