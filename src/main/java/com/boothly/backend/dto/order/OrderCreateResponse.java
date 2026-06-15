package com.boothly.backend.dto.order;

import com.boothly.backend.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class OrderCreateResponse {
    private final long id;
    private final String orderNumber;
    private final int salePostId;
    private final OrderStatus orderStatus;
    private final List<OrderItemCreateResponse> orderItems;
    private final FulfillmentInfoResponse fulfillmentInfo;
    private final PaymentInfoResponse paymentInfo;
    private final PriceInfoResponse priceInfo;
    private LocalDateTime createdAt;
}
