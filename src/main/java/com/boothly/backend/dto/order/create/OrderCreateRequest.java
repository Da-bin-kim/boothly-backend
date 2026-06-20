package com.boothly.backend.dto.order.create;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderCreateRequest {
    @NotBlank
    @Size(max = 50)
    private String payerName;

    @NotBlank
    @Size(max = 50)
    private String recipientName;

    @NotBlank
    @Size(max = 30)
    private String recipientPhone;

    @Positive
    private long deliveryMethodId;

    @NotBlank
    @Size(max = 255)
    private String deliveryAddress;

    @NotEmpty
    @Valid
    private List<OrderItemCreateRequest> orderItems;

    @Size(max = 255)
    private String memo;

    @NotBlank
    @Size(max = 50)
    private String bankName;

    @NotBlank
    @Size(max = 50)
    private String accountNumber;

    @NotBlank
    @Size(max = 50)
    private String accountHolder;
}
