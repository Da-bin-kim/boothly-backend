package com.boothly.backend.domain.order;

import com.boothly.backend.common.entity.BaseTimeEntity;
import com.boothly.backend.common.exception.order.InvalidOrderStatusTransitionException;
import com.boothly.backend.domain.enums.DeliveryType;
import com.boothly.backend.domain.enums.OrderStatus;
import com.boothly.backend.domain.salepost.SalePost;
import com.boothly.backend.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "orders")
public class Order extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sale_post_id", nullable = false)
    private SalePost salePost;

    @Column(name = "order_number", nullable = false, unique = true, length = 30)
    private String orderNumber;

    @Column(name = "payer_name", nullable = false, length = 50)
    private String payerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 20)
    private OrderStatus orderStatus;

    @Column(name = "recipient_name", nullable = false, length = 50)
    private String recipientName;

    @Column(name = "recipient_phone", nullable = false, length = 30)
    private String recipientPhone;

    @Column(name = "delivery_address", length = 255)
    private String deliveryAddress;

    @Column(name = "delivery_method_name", nullable = false, length = 50)
    private String deliveryMethodName;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type", nullable = false, length = 30)
    private DeliveryType deliveryType;

    @Column(name = "delivery_fee", nullable = false)
    private int deliveryFee;

    @Column(name = "tracking_number", length = 50)
    private String trackingNumber;

    @Column(name = "items_total_price", nullable = false)
    private int itemsTotalPrice;

    @Column(length = 255)
    private String memo;

    @Column(name = "bank_name", nullable = false, length = 50)
    private String bankName;

    @Column(name = "account_number", nullable = false, length = 50)
    private String accountNumber;

    @Column(name = "account_holder", nullable = false, length = 50)
    private String accountHolder;

    public void changeOrderStatus(OrderStatus orderStatus) {
        switch (orderStatus) {
            case REQUESTED -> request();
            case CONFIRMED -> confirm();
            case COMPLETED -> complete();
            default -> {
                throw new InvalidOrderStatusTransitionException(
                        this.orderStatus, orderStatus
                );
            }
        }
    }

    private void request() {
        if (orderStatus != OrderStatus.CONFIRMED) {
            throw new InvalidOrderStatusTransitionException(
                    orderStatus, OrderStatus.REQUESTED
            );
        }
        orderStatus = OrderStatus.REQUESTED;
    }
    private void confirm() {
        if (orderStatus != OrderStatus.REQUESTED) {
            throw new InvalidOrderStatusTransitionException(
                    orderStatus, OrderStatus.CONFIRMED
            );
        }
        orderStatus = OrderStatus.CONFIRMED;
    }
    private void complete() {
        if (orderStatus != OrderStatus.CONFIRMED) {
            throw new InvalidOrderStatusTransitionException(
                    orderStatus, OrderStatus.COMPLETED
            );
        }
        orderStatus = OrderStatus.COMPLETED;
    }
}
