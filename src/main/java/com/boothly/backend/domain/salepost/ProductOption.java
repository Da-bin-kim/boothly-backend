package com.boothly.backend.domain.salepost;

import com.boothly.backend.common.entity.BaseTimeEntity;
import com.boothly.backend.common.exception.ErrorCode;
import com.boothly.backend.common.exception.salepost.ProductOptionException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(
        name = "product_options"
)
public class ProductOption extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sale_post_id", nullable = false)
    private SalePost salePost;

    @Builder.Default
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

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void update(
            ProductOptionUpdateValues updateValues,
            boolean hasExistingOrders){
        validateNotDeleted();

        if (hasExistingOrders) {
            updateWithExistingOrders(updateValues);
        } else {
            updateWithoutOrders(updateValues);
        }
    }


    private void updateWithExistingOrders(ProductOptionUpdateValues updateValues) {
        if (updateValues.isDeleted()) {
            throw new ProductOptionException(ErrorCode.PRODUCT_OPTION_DELETE_WITH_ORDERS_NOT_ALLOWED, salePost.getId(), getId());
        }

        if (!updateValues.isActive()) {
            validateOnlyActiveMayChange(updateValues);
            changeActiveStatus(false);
            return;
        }

        validateEditableFieldsUnchanged(updateValues);
        updateStockQuantity(updateValues.stockQuantity());
        changeActiveStatus(true);
    }

    private void updateWithoutOrders(ProductOptionUpdateValues updateValues) {
        if (updateValues.isDeleted()) {
            validateOnlyDeletedChanged(updateValues);
            softDelete();
            return;
        }

        if (!updateValues.isActive()) {
            validateOnlyActiveMayChange(updateValues);
            changeActiveStatus(false);
            return;
        }

        updateEditableFields(updateValues);
        updateStockQuantity(updateValues.stockQuantity());
        changeActiveStatus(true);
    }

    private boolean isActiveChanged(ProductOptionUpdateValues updateValues) {
        return active != updateValues.isActive();
    }

    private void validateNotDeleted() {
        if (deletedAt != null) {
            throw new ProductOptionException(
                    ErrorCode.PRODUCT_OPTION_ALREADY_DELETED,
                    salePost.getId(),
                    getId());
        }
    }

    private void validateOnlyDeletedChanged(ProductOptionUpdateValues updateValues) {
        validateEditableFieldsUnchanged(updateValues);
        validateStockQuantityUnchanged(updateValues.stockQuantity());

        if (isActiveChanged(updateValues)) {
            throw new ProductOptionException(
                    ErrorCode.PRODUCT_OPTION_DELETE_WITH_ACTIVE_CHANGE_NOT_ALLOWED,
                    salePost.getId(),
                    getId());
        }
    }

    private void validateOnlyActiveMayChange(ProductOptionUpdateValues updateValues) {
        validateEditableFieldsUnchanged(updateValues);
        validateStockQuantityUnchanged(updateValues.stockQuantity());
    }

    private void validateEditableFieldsUnchanged(ProductOptionUpdateValues updateValues) {
        if (!Objects.equals(description, updateValues.description())
                || !Objects.equals(imageUrl, updateValues.imageUrl())
                || displayOrder != updateValues.displayOrder()
                || !Objects.equals(name, updateValues.name())
                || price != updateValues.price()
                || !Objects.equals(cost, updateValues.cost())
                || !Objects.equals(maxQuantityPerOrder, updateValues.maxQuantityPerOrder())
        ) {
            throw new ProductOptionException(
                    ErrorCode.PRODUCT_OPTION_EDITABLE_FIELDS_CHANGE_NOT_ALLOWED,
                    salePost.getId(),
                    getId());
        }
    }

    private void validateStockQuantityUnchanged(int stockQuantity) {
        if (this.stockQuantity != stockQuantity) {
            throw new ProductOptionException(
                    ErrorCode.PRODUCT_OPTION_EDITABLE_FIELDS_CHANGE_NOT_ALLOWED,
                    salePost.getId(),
                    getId());
        }
    }

    private void softDelete() {
        deletedAt = LocalDateTime.now();
    }

    private void changeActiveStatus(boolean active) {
        this.active = active;
    }

    private void updateEditableFields(ProductOptionUpdateValues updateValues) {
        description = updateValues.description();
        imageUrl = updateValues.imageUrl();
        displayOrder = updateValues.displayOrder();
        name = updateValues.name();
        price = updateValues.price();
        cost = updateValues.cost();
        maxQuantityPerOrder = updateValues.maxQuantityPerOrder();
    }

    private void updateStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
