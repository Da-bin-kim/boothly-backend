package com.boothly.backend.dto.order.list;

import com.boothly.backend.domain.enums.OrderStatus;
import com.boothly.backend.domain.order.Order;
import com.boothly.backend.domain.order.OrderItem;
import com.boothly.backend.dto.order.common.FulfillmentInfoResponse;
import com.boothly.backend.dto.order.common.PaymentInfoResponse;
import com.boothly.backend.dto.order.detail.OrderItemDetailResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class SellerSalePostOrderResponse {
    private Long orderId;
    private String orderNumber;
    private OrderStatus orderStatus;

    private PaymentInfoResponse paymentInfo;
    private FulfillmentInfoResponse fulfillmentInfo;
    private List<OrderItemListResponse> orderItems;

    private String memo;

    public static SellerSalePostOrderResponse from(Order order, List<OrderItem> orderItems) {
        PaymentInfoResponse paymentInfo = PaymentInfoResponse.from(order);
        FulfillmentInfoResponse fulfillment = FulfillmentInfoResponse.from(order);
        List<OrderItemListResponse> orderItemListResponses =
                orderItems.stream().map(OrderItemListResponse::from).toList();

        return SellerSalePostOrderResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderStatus(order.getOrderStatus())
                .paymentInfo(paymentInfo)
                .fulfillmentInfo(fulfillment)
                .orderItems(orderItemListResponses)
                .memo(order.getMemo())
                .build();
    }
}
