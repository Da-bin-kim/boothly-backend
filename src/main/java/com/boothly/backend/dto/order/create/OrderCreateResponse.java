package com.boothly.backend.dto.order.create;

import com.boothly.backend.domain.enums.OrderStatus;
import com.boothly.backend.domain.order.Order;
import com.boothly.backend.domain.order.OrderItem;
import com.boothly.backend.dto.order.common.FulfillmentInfoResponse;
import com.boothly.backend.dto.order.common.PaymentInfoResponse;
import com.boothly.backend.dto.order.common.PriceInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class OrderCreateResponse {
    private final long orderId;
    private final String orderNumber;
    private final long salePostId;
    private final OrderStatus orderStatus;
    private final List<OrderItemCreateResponse> orderItems;
    private final FulfillmentInfoResponse fulfillmentInfo;
    private final PaymentInfoResponse paymentInfo;
    private final PriceInfoResponse priceInfo;
    private LocalDateTime createdAt;

    public static OrderCreateResponse from(Order order,List<OrderItem> orderItems) {
        List<OrderItemCreateResponse> orderItemCreateResponses = orderItems
                .stream()
                .map(OrderItemCreateResponse::from)
                .collect(Collectors.toList());

        FulfillmentInfoResponse fulfillmentInfoResponse= FulfillmentInfoResponse.from(order);
        PaymentInfoResponse paymentInfoResponse= PaymentInfoResponse.from(order);
        PriceInfoResponse priceInfoResponse = PriceInfoResponse.from(order);

        return OrderCreateResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .salePostId(order.getSalePost().getId())
                .orderStatus(order.getOrderStatus())
                .orderItems(orderItemCreateResponses)
                .fulfillmentInfo(fulfillmentInfoResponse)
                .paymentInfo(paymentInfoResponse)
                .priceInfo(priceInfoResponse)
                .createdAt(order.getCreatedAt())
                .build();
    }
}
