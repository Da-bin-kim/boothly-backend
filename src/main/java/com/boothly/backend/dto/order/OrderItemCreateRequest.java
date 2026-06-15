package com.boothly.backend.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OrderItemCreateRequest {
    private final Long productOptionId;
    private final int quantity;
}
