package com.boothly.backend.dto.salepost;

import com.boothly.backend.domain.enums.SalePostStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class SalePostCreateRequest {
    private String title;
    private String content;
    private SalePostStatus saleStatus;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDate fulfillmentDate;
    private String thumbnailImageUrl;
    private String bankName;
    private String accountNumber;
    private String accountHolder;
}
