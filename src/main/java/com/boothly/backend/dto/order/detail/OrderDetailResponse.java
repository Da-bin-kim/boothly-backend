package com.boothly.backend.dto.order.detail;

import com.boothly.backend.domain.enums.OrderStatus;
import com.boothly.backend.domain.order.Order;
import com.boothly.backend.domain.order.OrderItem;
import com.boothly.backend.dto.order.common.FulfillmentInfoResponse;
import com.boothly.backend.dto.order.common.PaymentInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class OrderDetailResponse {
    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private OrderSalePostDetailResponse salePost;
    private List<OrderItemDetailResponse> orderItems;
    private PaymentInfoResponse paymentInfo;
    private FulfillmentInfoResponse fulfillmentInfo;
    private String trackingNumber;
    private String memo;

    public static OrderDetailResponse from(Order order, List<OrderItem> orderItems) {
        OrderSalePostDetailResponse orderSalePostDetailResponse = OrderSalePostDetailResponse.from(order.getSalePost());
        List<OrderItemDetailResponse> orderItemDetailResponses =
                orderItems.stream()
                        .map(OrderItemDetailResponse::from)
                        .collect(Collectors.toList());

        return OrderDetailResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getOrderStatus())
                .salePost(orderSalePostDetailResponse)
                .orderItems(orderItemDetailResponses)
                .paymentInfo(PaymentInfoResponse.from(order))
                .fulfillmentInfo(FulfillmentInfoResponse.from(order))
                .trackingNumber(order.getTrackingNumber())
                .memo(order.getMemo())
                .build();
    }
}
