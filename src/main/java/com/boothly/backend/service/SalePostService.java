package com.boothly.backend.service;

import com.boothly.backend.domain.salepost.DeliveryMethod;
import com.boothly.backend.domain.salepost.ProductOption;
import com.boothly.backend.domain.salepost.SalePost;
import com.boothly.backend.domain.user.User;
import com.boothly.backend.dto.salepost.DeliveryMethodCreateRequest;
import com.boothly.backend.dto.salepost.ProductOptionCreateRequest;
import com.boothly.backend.dto.salepost.SalePostCreateRequest;
import com.boothly.backend.dto.salepost.SalePostListResponse;
import com.boothly.backend.repository.DeliveryMethodRepository;
import com.boothly.backend.repository.ProductOptionRepository;
import com.boothly.backend.repository.SalePostRepository;
import com.boothly.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalePostService {
    private final SalePostRepository salePostRepository;
    private final UserRepository userRepository;
    private final ProductOptionRepository productOptionRepository;
    private final DeliveryMethodRepository deliveryMethodRepository;

    /**
     * 판매글을 생성한다.
     *
     * @param sellerId 판매자 ID
     * @param request 판매글 생성 요청 DTO
     * @return 생성된 판매글 ID
     */
    @Transactional
    public Long create(Long sellerId, SalePostCreateRequest request) {
        /* 유효성 검증 */
        User seller = userRepository.findById(sellerId).orElseThrow(() ->
                new EntityNotFoundException("사용자를 찾을 수 없습니다: " + sellerId));
        validateSalePeriod(request);

        /* DTO -> Entity */
        SalePost salePost = SalePost.builder()
                .user(seller)
                .title(request.getTitle())
                .content(request.getContent())
                .saleStatus(request.getSaleStatus())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .fulfillmentDate(request.getFulfillmentDate())
                .thumbnailImageUrl(request.getThumbnailImageUrl())
                .bankName(request.getBankName())
                .accountNumber(request.getAccountNumber())
                .accountHolder(request.getAccountHolder())
                .build();

        /* DB 저장 */
        SalePost savedSalePost = salePostRepository.save(salePost);
        saveProductOptions(savedSalePost, request.getProductOptions());
        saveDeliveryMethods(savedSalePost, request.getDeliveryMethods());

        return savedSalePost.getId();
    }

    private void validateSalePeriod(SalePostCreateRequest request) {
        if (request.getEndAt() != null
                && request.getEndAt().isBefore(request.getStartAt())) {
            throw new IllegalArgumentException(
                    "판매 종료 시각은 판매 시작 시각보다 빠를 수 없습니다."
            );
        }
    }

    /* 상품 옵션 저장 메서드 */
    private void saveProductOptions(
            SalePost salePost,
            List<ProductOptionCreateRequest> requests
    ) {
        if (requests == null || requests.isEmpty()) {
            return;
        }

        List<ProductOption> productOptions = requests.stream()
                .map(request -> ProductOption.builder()
                        .salePost(salePost)
                        .active(request.isActive())
                        .displayOrder(request.getDisplayOrder())
                        .name(request.getName())
                        .description(request.getDescription())
                        .imageUrl(request.getImageUrl())
                        .price(request.getPrice())
                        .cost(request.getCost())
                        .stockQuantity(request.getStockQuantity())
                        .maxQuantityPerOrder(
                                request.getMaxQuantityPerOrder()
                        )
                        .build())
                .toList();

        productOptionRepository.saveAll(productOptions);
    }

    /* 배송 방법 저장 메서드 */
    private void saveDeliveryMethods(
            SalePost salePost,
            List<DeliveryMethodCreateRequest> requests
    ) {
        if (requests == null || requests.isEmpty()) {
            return;
        }

        List<DeliveryMethod> deliveryMethods = requests.stream()
                .map(request -> DeliveryMethod.builder()
                        .salePost(salePost)
                        .methodName(request.getMethodName())
                        .deliveryType(request.getDeliveryType())
                        .deliveryFee(request.getDeliveryFee())
                        .active(request.isActive())
                        .build())
                .toList();

        deliveryMethodRepository.saveAll(deliveryMethods);
    }


    public Page<SalePostListResponse> findAll(
            String keyword,
            Pageable pageable
    ){
        return salePostRepository
                .findByTitleContaining(keyword, pageable)
                .map(SalePostListResponse::from);
    }
}
