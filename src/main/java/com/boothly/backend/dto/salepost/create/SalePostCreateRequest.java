package com.boothly.backend.dto.salepost.create;

import com.boothly.backend.domain.enums.SalePostStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class SalePostCreateRequest {
    @NotBlank
    @Size(max = 100)
    private String title;

    @NotNull
    private String content;

    @NotNull
    private SalePostStatus saleStatus;

    @NotNull
    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private LocalDate fulfillmentDate;

    @Size(max = 255)
    private String thumbnailImageUrl;

    @NotBlank
    @Size(max = 50)
    private String bankName;

    @NotBlank
    @Size(max = 50)
    private String accountNumber;

    @NotBlank
    @Size(max = 50)
    private String accountHolder;

    @Valid
    @NotEmpty
    private List<ProductOptionCreateRequest> productOptions;

    @Valid
    @NotEmpty
    private List<DeliveryMethodCreateRequest> deliveryMethods;
}
