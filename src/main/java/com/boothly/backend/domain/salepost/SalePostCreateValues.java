package com.boothly.backend.domain.salepost;

import com.boothly.backend.domain.enums.SalePostStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SalePostCreateValues (
        String title,
        String content,
        SalePostStatus saleStatus,
        LocalDateTime startAt,
        LocalDateTime endAt,
        LocalDate fulfillmentDate,
        String thumbnailImageUrl,
        String bankName,
        String accountNumber,
        String accountHolder
) {
}
