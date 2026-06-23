package com.boothly.backend.dto.salepost.detail;

import com.boothly.backend.domain.enums.SalePostStatus;
import com.boothly.backend.domain.salepost.DeliveryMethod;
import com.boothly.backend.domain.salepost.ProductOption;
import com.boothly.backend.domain.salepost.SalePost;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SalePostDetailResponse {
    private final long id;
    private final SellerResponse seller;
    private final String title;
    private final String content;
    private final SalePostStatus saleStatus;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final LocalDate fulfillmentDate;
    private final String thumbnailImageUrl;
    private final String bankName;
    private final String accountNumber;
    private final String accountHolder;

    private final List<ProductOptionResponse> productOptions;
    private final List<DeliveryMethodResponse> deliveryMethods;

    public static SalePostDetailResponse from(SalePost salePost, List<ProductOption> productOptions, List<DeliveryMethod> deliveryMethods) {
        List<ProductOptionResponse> productOptionResponses =
                productOptions.stream()
                        .map(ProductOptionResponse::from)
                        .collect(Collectors.toList());

        List<DeliveryMethodResponse> deliveryMethodResponses =
                deliveryMethods.stream()
                        .map(DeliveryMethodResponse::from)
                        .collect(Collectors.toList());

        return SalePostDetailResponse.builder()
                .id(salePost.getId())
                .seller(SellerResponse.from(salePost.getUser()))
                .title(salePost.getTitle())
                .content(salePost.getContent())
                .saleStatus(salePost.getSaleStatus())
                .startAt(salePost.getStartAt())
                .endAt(salePost.getEndAt())
                .fulfillmentDate(salePost.getFulfillmentDate())
                .thumbnailImageUrl(salePost.getThumbnailImageUrl())
                .bankName(salePost.getBankName())
                .accountNumber(salePost.getAccountNumber())
                .accountHolder(salePost.getAccountHolder())
                .productOptions(productOptionResponses)
                .deliveryMethods(deliveryMethodResponses)
                .build();
    }

    @Builder
    private SalePostDetailResponse(long id, SellerResponse seller, String title, String content, SalePostStatus saleStatus, LocalDateTime startAt, LocalDateTime endAt, LocalDate fulfillmentDate, String thumbnailImageUrl, String bankName, String accountNumber, String accountHolder, List<ProductOptionResponse> productOptions, List<DeliveryMethodResponse> deliveryMethods) {
        this.id = id;
        this.seller = seller;
        this.title = title;
        this.content = content;
        this.saleStatus = saleStatus;
        this.startAt = startAt;
        this.endAt = endAt;
        this.fulfillmentDate = fulfillmentDate;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.productOptions = productOptions;
        this.deliveryMethods = deliveryMethods;
    }
}
