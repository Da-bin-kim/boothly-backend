package com.boothly.backend.domain.salepost;

import com.boothly.backend.common.entity.BaseTimeEntity;
import com.boothly.backend.domain.enums.DeliveryType;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Entity
@Table(name = "delivery_methods")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DeliveryMethod extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sale_post_id", nullable = false)
    private SalePost salePost;

    @Column(name = "method_name", nullable = false, length = 50)
    private String methodName;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type", nullable = false, length = 30)
    private DeliveryType deliveryType;

    @Column(name = "delivery_fee", nullable = false)
    private int deliveryFee = 0;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
