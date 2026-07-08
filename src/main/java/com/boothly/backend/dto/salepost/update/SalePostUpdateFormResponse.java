package com.boothly.backend.dto.salepost.update;

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
@Builder
public class SalePostUpdateFormResponse {
    private final Long salePostId;
    private final String title;
    private final String content;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final LocalDate fulfillmentDate;
    private final String thumbnailImageUrl;
    private final String bankName;
    private final String accountNumber;
    private final String accountHolder;
    private final List<DeliveryMethodFormResponse> deliveryMethods;
    private final List<ProductOptionUpdateFormResponse> productOptions;
    private final boolean hasOrder;

    public static SalePostUpdateFormResponse from(
            SalePost salePost,
            List<DeliveryMethod> deliveryMethods,
            List<ProductOption> productOptions,
            boolean hasOrder
    ){

        List<DeliveryMethodFormResponse> deliveryMethodFormResponses =
                deliveryMethods.stream()
                    .map(DeliveryMethodFormResponse::from)
                    .collect(Collectors.toList());

        List<ProductOptionUpdateFormResponse> productOptionUpdateFormResponses =
                productOptions.stream()
                        .map(ProductOptionUpdateFormResponse::from)
                        .collect(Collectors.toList());

        return SalePostUpdateFormResponse.builder()
                .salePostId(salePost.getId())
                .title(salePost.getTitle())
                .content(salePost.getContent())
                .startAt(salePost.getStartAt())
                .endAt(salePost.getEndAt())
                .fulfillmentDate(salePost.getFulfillmentDate())
                .thumbnailImageUrl(salePost.getThumbnailImageUrl())
                .bankName(salePost.getBankName())
                .accountNumber(salePost.getAccountNumber())
                .accountHolder(salePost.getAccountHolder())
                .deliveryMethods(deliveryMethodFormResponses)
                .productOptions(productOptionUpdateFormResponses)
                .hasOrder(hasOrder)
                .build();
    }
}
