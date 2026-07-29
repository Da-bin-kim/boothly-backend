package com.boothly.backend.domain.salepost;

import com.boothly.backend.common.entity.BaseTimeEntity;
import com.boothly.backend.common.exception.ErrorCode;
import com.boothly.backend.common.exception.salepost.InsufficientProductOptionStockException;
import com.boothly.backend.common.exception.salepost.InvalidSalePostOptionException;
import com.boothly.backend.common.exception.salepost.ProductOptionException;
import com.boothly.backend.common.exception.salepost.ProductOptionQuantityLimitExceededException;
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

    private enum ProductOptionOperation {
        UPDATE,
        DEACTIVATE,
        DELETE
    }

    public void update(
            ProductOptionUpdateValues updateValues,
            boolean hasExistingOrders) {

        validateNotDeleted();
        ProductOptionOperation operation = determineOperation(updateValues);

        switch (operation) {
            case DELETE -> delete(updateValues, hasExistingOrders);
            case DEACTIVATE -> deactivate(updateValues);
            case UPDATE -> updateNormally(updateValues, hasExistingOrders);
        }
    }

    private ProductOptionOperation determineOperation (ProductOptionUpdateValues updateValues) {
        if(updateValues.isDeleted()){
            return ProductOptionOperation.DELETE;
        }
        if(!updateValues.isActive()){
            return ProductOptionOperation.DEACTIVATE;
        }
        return ProductOptionOperation.UPDATE;
    }

    private void delete(
            ProductOptionUpdateValues updateValues,
            boolean hasExistingOrders){

        if(hasExistingOrders){
            throw new ProductOptionException(
                    ErrorCode.PRODUCT_OPTION_DELETE_WITH_ORDERS_NOT_ALLOWED,
                    salePost.getId(),
                    getId()
            );
        }

        validateOnlyDeletedChanged(updateValues);
        softDelete();
    }

    private void deactivate(
            ProductOptionUpdateValues updateValues
    ){
        validateOnlyActiveMayChange(updateValues);
        changeActiveStatus(false);
    }

    private void updateNormally(
            ProductOptionUpdateValues updateValues,
            boolean hasExistingOrders) {

        if(hasExistingOrders) {
            validateEditableFieldsUnchanged(updateValues);
        }else {
            updateEditableFields(updateValues);
        }
        updateStockQuantity(updateValues.stockQuantity());
        changeActiveStatus(true);
    }

    public void decreaseStock(int orderQuantity) {
        validateOrderQuantity(orderQuantity);
        validateAvailableForSale();
        validateSufficientStock(orderQuantity);

        stockQuantity -= orderQuantity;
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
        if (isActiveChanged(updateValues)) {
            throw new ProductOptionException(
                    ErrorCode.PRODUCT_OPTION_DELETE_WITH_ACTIVE_CHANGE_NOT_ALLOWED,
                    salePost.getId(),
                    getId());
        }

        validateEditableFieldsUnchanged(updateValues);
        validateStockQuantityUnchanged(updateValues.stockQuantity());
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

    // 재고 관련 검증 함수
    private void validateAvailableForSale() {
        if (!active || deletedAt != null) {
            throw new InvalidSalePostOptionException(
                    salePost.getId(),
                    id
            );
        }
    }

    private void validateSufficientStock(int orderQuantity) {
        if (stockQuantity < orderQuantity) {
            throw new InsufficientProductOptionStockException(
                    id,
                    orderQuantity,
                    stockQuantity
            );
        }
    }

    private void validateOrderQuantity(int orderQuantity) {
        if (orderQuantity <= 0) {
            throw new IllegalArgumentException(
                    "주문 수량은 1개 이상이어야 합니다."
            );
        }

        if (maxQuantityPerOrder != null
                && orderQuantity > maxQuantityPerOrder) {
            throw new ProductOptionQuantityLimitExceededException(
                    id,
                    orderQuantity,
                    maxQuantityPerOrder
            );
        }
    }
}
