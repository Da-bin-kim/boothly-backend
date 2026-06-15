package com.boothly.backend.dto.order;

import lombok.Getter;

import java.util.List;

@Getter
public class OrderCreateRequest {
    private String payerName;
    private String recipientName;
    private String recipientPhone;
    private int deliveryMethodId;
    private String deliveryAddress;
    private List<OrderItemCreateRequest> orderItems;
    private String memo;
}
