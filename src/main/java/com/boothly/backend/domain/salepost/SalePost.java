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
@AllArgsConstructor
@Builder
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
    private void close() {
        if (saleStatus != SalePostStatus.OPEN) {
            throw new InvalidSaleStatusTransitionException(
                    saleStatus,
                    SalePostStatus.CLOSED
            );
        }

        saleStatus = SalePostStatus.CLOSED;
        endAt = LocalDateTime.now();
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
}
