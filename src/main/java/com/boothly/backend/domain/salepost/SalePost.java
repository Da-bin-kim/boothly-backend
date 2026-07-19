package com.boothly.backend.domain.salepost;

import com.boothly.backend.common.entity.BaseTimeEntity;
import com.boothly.backend.common.exception.salepost.InvalidSaleStatusTransitionException;
import com.boothly.backend.domain.enums.SalePostStatus;
import com.boothly.backend.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "sale_posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SalePost extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private SalePostStatus saleStatus;

    @Column(nullable = false)
    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private LocalDate fulfillmentDate;

    @Column(length = 255)
    private String thumbnailImageUrl;

    @Column(nullable = false, length = 50)
    private String bankName;

    @Column(nullable = false, length = 50)
    private String accountNumber;

    @Column(nullable = false, length = 50)
    private String accountHolder;


    public static SalePost create(
            User seller,
            SalePostCreateValues createValues
    ) {
        validateSalePeriod(createValues.startAt(), createValues.endAt());
        return new SalePost(seller, createValues);
    }

    public void update(SalePostUpdateValues updateValues) {
        validateSalePeriod(updateValues.startAt(), updateValues.endAt());

        title = updateValues.title();
        content = updateValues.content();
        startAt = updateValues.startAt();
        endAt = updateValues.endAt();
        fulfillmentDate = updateValues.fulfillmentDate();
        thumbnailImageUrl = updateValues.thumbnailImageUrl();
        bankName = updateValues.bankName();
        accountNumber = updateValues.accountNumber();
        accountHolder = updateValues.accountHolder();
    }

    public void changeStatus(SalePostStatus targetStatus) {
        switch (targetStatus) {
            case CLOSED -> close();
            case HIDDEN -> hide();
            default -> throw new InvalidSaleStatusTransitionException(
                    saleStatus,
                    targetStatus
            );
        }
    }

    private SalePost(
            User seller,
            SalePostCreateValues createValues
    ) {
        this.user = seller;
        this.title = createValues.title();
        this.content = createValues.content();
        this.saleStatus = createValues.saleStatus();
        this.startAt = createValues.startAt();
        this.endAt = createValues.endAt();
        this.fulfillmentDate = createValues.fulfillmentDate();
        this.thumbnailImageUrl = createValues.thumbnailImageUrl();
        this.bankName = createValues.bankName();
        this.accountNumber = createValues.accountNumber();
        this.accountHolder = createValues.accountHolder();
    }

    private void close() {
        if (saleStatus != SalePostStatus.OPEN) {
            throw new InvalidSaleStatusTransitionException(
                    saleStatus,
                    SalePostStatus.CLOSED
            );
        }
        LocalDateTime closedAt = LocalDateTime.now();
        validateSalePeriod(startAt, closedAt);

        saleStatus = SalePostStatus.CLOSED;
        endAt = closedAt;
    }

    private void hide(){
        if (saleStatus != SalePostStatus.CLOSED) {
            throw new InvalidSaleStatusTransitionException(
                    saleStatus,
                    SalePostStatus.HIDDEN
            );
        }

        saleStatus = SalePostStatus.HIDDEN;
    }

    private static void validateSalePeriod(
            LocalDateTime startAt,
            LocalDateTime endAt)
    {
        if (endAt != null
                && endAt.isBefore(startAt)) {
            throw new IllegalArgumentException(
                    "판매 종료 시각은 판매 시작 시각보다 빠를 수 없습니다."
            );
        }
    }
}
