package com.boothly.backend.domain.salepost;

import com.boothly.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(
        name = "product_options",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_PRODUCT_OPTIONS_DISPLAY_ORDER",
                        columnNames = {"sale_post_id", "display_order"}
                )
        }
)
public class ProductOption extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_post_id")
    private SalePost salePost;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private int displayOrder;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private int price;

    @Column
    private Integer cost;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @Column(name = "max_quantity_per_order")
    private Integer maxQuantityPerOrder;
}
