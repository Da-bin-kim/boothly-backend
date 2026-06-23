package com.boothly.backend.domain.salepost;

import com.boothly.backend.common.entity.BaseTimeEntity;
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

    public void close() {
        if (this.saleStatus == SalePostStatus.CLOSED) {
            return;
        }

        this.saleStatus = SalePostStatus.CLOSED;
        this.endAt = LocalDateTime.now();
    }
}
