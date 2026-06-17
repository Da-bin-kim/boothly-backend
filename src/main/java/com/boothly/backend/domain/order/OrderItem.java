package com.boothly.backend.domain.order;

import com.boothly.backend.common.entity.BaseTimeEntity;
import com.boothly.backend.domain.salepost.ProductOption;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "order_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_ORDER_ITEMS_ORDER_OPTION",
                        columnNames = {"order_id", "product_option_id"}
                )
        }
)
public class OrderItem extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_option_id", nullable = false)
    private ProductOption productOption;

    @Column(name = "product_option_name", nullable = false, length = 100)
    private String productOptionName;

    @Column(name = "unit_price", nullable = false)
    private int unitPrice;

    @Column(name = "unit_cost")
    private Integer unitCost;

    @Column(nullable = false)
    private int quantity;
}
