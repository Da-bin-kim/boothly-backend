package com.boothly.backend.dto.order.detail;

import com.boothly.backend.domain.enums.OrderStatus;
import com.boothly.backend.dto.order.common.FulfillmentInfoResponse;
import com.boothly.backend.dto.order.common.PaymentInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class OrderDetailResponse {
    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private OrderSalePostDetailResponse salePost;
    private List<OrderItemDtailResponse> orderItems;
    private PaymentInfoResponse paymentInfo;
    private FulfillmentInfoResponse fulfillmentInfo;
    private String trackingNumber;
    private String memo;
}
