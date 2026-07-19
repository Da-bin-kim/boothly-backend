package com.boothly.backend.domain.salepost;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SalePostUpdateValues(
        String title,
        String content,
        LocalDateTime startAt,
        LocalDateTime endAt,
        LocalDate fulfillmentDate,
        String thumbnailImageUrl,
        String bankName,
        String accountNumber,
        String accountHolder
) {}
