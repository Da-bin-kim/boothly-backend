package com.boothly.backend.domain.salepost;

import com.boothly.backend.common.entity.BaseTimeEntity;
import com.boothly.backend.common.exception.ErrorCode;
import com.boothly.backend.common.exception.salepost.DeliveryMethodException;
import com.boothly.backend.domain.enums.DeliveryType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

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

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    private enum DeliveryMethodOperation{
        UPDATE,
        DEACTIVATE,
        DELETE
    }

    public void update(DeliveryMethodUpdateValues updateValues, boolean hasExistingOrders) {
        validateNotDeleted();

        DeliveryMethodOperation operation = determineOperation(updateValues);
        switch (operation) {
            case DELETE -> delete(updateValues, hasExistingOrders);
            case DEACTIVATE -> deactivate(updateValues);
            case UPDATE -> updateNormally(updateValues, hasExistingOrders);
        }
    }

    private void delete(
            DeliveryMethodUpdateValues updateValues
            , boolean hasExistingOrders) {
        if (hasExistingOrders) {
            throw new DeliveryMethodException(
                    ErrorCode.DELIVERY_METHOD_DELETE_WITH_ORDERS_NOT_ALLOWED
                    , salePost.getId()
                    , getId()
            );
        }

        validateOnlyDeletedChanged(updateValues);
        softDelete();
    }

    private void deactivate (DeliveryMethodUpdateValues updateValues) {
        validateOnlyActiveMayChanged(updateValues);
        changeActiveStatus(false);
    }

    private void updateNormally(
            DeliveryMethodUpdateValues updateValues
            , boolean hasExistingOrders) {
        if (hasExistingOrders) {
            validateEditableFieldsUnchanged(updateValues);
        } else {
            updateEditableFields(updateValues);
        }
        changeActiveStatus(true);
    }

    private DeliveryMethodOperation determineOperation (DeliveryMethodUpdateValues updateValues) {
        if(updateValues.isDeleted()) {
            return DeliveryMethodOperation.DELETE;
        }
        if(!updateValues.isActive()) {
            return DeliveryMethodOperation.DEACTIVATE;
        }
        return DeliveryMethodOperation.UPDATE;
    }

    private void validateNotDeleted() {
        if (deletedAt != null) {
           throw new DeliveryMethodException(
                   ErrorCode.DELIVERY_METHOD_ALREADY_DELETED
                , salePost.getId()
                , getId()
           );
        }
    }

    boolean isActiveChanged(DeliveryMethodUpdateValues updateValues) {
        return active != updateValues.isActive();
    }

    private void validateOnlyDeletedChanged(DeliveryMethodUpdateValues updateValues) {
        if (isActiveChanged(updateValues)) {
            throw new DeliveryMethodException(
                    ErrorCode.DELIVERY_METHOD_DELETE_WITH_ACTIVE_CHANGE_NOT_ALLOWED
                    , salePost.getId()
                    , getId()
            );
        }
        validateEditableFieldsUnchanged(updateValues);
    }

    private void validateOnlyActiveMayChanged(DeliveryMethodUpdateValues updateValues){
        validateEditableFieldsUnchanged(updateValues);
    }

    private void validateEditableFieldsUnchanged(DeliveryMethodUpdateValues updateValues) {
        if(!Objects.equals(getMethodName(), updateValues.methodName())
                || !Objects.equals(getDeliveryType(), updateValues.deliveryType())
                || getDeliveryFee() != updateValues.deliveryFee()
        ){
            throw new DeliveryMethodException(
                    ErrorCode.DELIVERY_METHOD_EDITABLE_FIELDS_CHANGE_NOT_ALLOWED
                    , salePost.getId()
                    , getId()
            );
        }
    }

    private void softDelete() {
        deletedAt = LocalDateTime.now();
    }

    private void changeActiveStatus(boolean active) {
        this.active = active;
    }

    private void updateEditableFields(DeliveryMethodUpdateValues updateValues) {
        methodName = updateValues.methodName();
        deliveryType = updateValues.deliveryType();
        deliveryFee = updateValues.deliveryFee();
    }
}
