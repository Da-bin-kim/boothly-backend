package com.boothly.backend.domain.salepost;

import com.boothly.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;

import java.util.Objects;

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

    public void update(ProductOptionUpdateValues values, boolean hasExistingOrders){
        if(hasExistingOrders){
            validateOrderSensitiveFieldsUnchanged(values);
        }else{
            updateOrderSensitiveFields(values);
        }
        updateAlwaysEditableFields(values);
    }

    private void validateOrderSensitiveFieldsUnchanged(ProductOptionUpdateValues values){
        if (!Objects.equals(displayOrder, values.displayOrder())
        || !Objects.equals(name, values.name())
        || !Objects.equals(price, values.price())
        || !Objects.equals(cost, values.cost())
        || !Objects.equals(maxQuantityPerOrder, values.maxQuantityPerOrder())
        ) {
            // 예외
        }
    }
    private void updateOrderSensitiveFields(ProductOptionUpdateValues values){
        displayOrder = values.displayOrder();
        name = values.name();
        price = values.price();
        cost = values.cost();
        maxQuantityPerOrder = values.maxQuantityPerOrder();
    }
    private void updateAlwaysEditableFields(ProductOptionUpdateValues values){
        description = values.description();
        imageUrl = values.imageUrl();
        stockQuantity = values.stockQuantity();
    }
}
