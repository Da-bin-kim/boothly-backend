package com.boothly.backend.service;

import com.boothly.backend.common.exception.salepost.InvalidSalePostStateException;
import com.boothly.backend.common.exception.salepost.SalePostNotFoundException;
import com.boothly.backend.common.exception.user.UserNotFoundException;
import com.boothly.backend.domain.enums.SalePostStatus;
import com.boothly.backend.domain.salepost.DeliveryMethod;
import com.boothly.backend.domain.salepost.ProductOption;
import com.boothly.backend.domain.salepost.SalePost;
import com.boothly.backend.domain.user.User;
import com.boothly.backend.dto.salepost.SalePostStatusChangeResponse;
import com.boothly.backend.dto.salepost.create.DeliveryMethodCreateRequest;
import com.boothly.backend.dto.salepost.create.ProductOptionCreateRequest;
import com.boothly.backend.dto.salepost.create.SalePostCreateRequest;
import com.boothly.backend.dto.salepost.create.SalePostCreateResponse;
import com.boothly.backend.dto.salepost.detail.SalePostDetailResponse;
import com.boothly.backend.dto.salepost.list.MySalePostOverviewResponse;
import com.boothly.backend.dto.salepost.list.SalePostListResponse;
import com.boothly.backend.repository.DeliveryMethodRepository;
import com.boothly.backend.repository.ProductOptionRepository;
import com.boothly.backend.repository.SalePostRepository;
import com.boothly.backend.repository.UserRepository;
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
    public SalePostCreateResponse createSalePost(Long sellerId, SalePostCreateRequest request) {
        /* 유효성 검증 */
        User seller = userRepository.findById(sellerId).orElseThrow(() ->
                new UserNotFoundException(sellerId));
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

        return new SalePostCreateResponse(savedSalePost.getId());
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


    public Page<SalePostListResponse> findOpenSalePosts(
            String keyword,
            Pageable pageable
    ){
        return salePostRepository
                .findBySaleStatusAndTitleContaining(SalePostStatus.OPEN, keyword, pageable)
                .map(SalePostListResponse::from);
    }

    public SalePostDetailResponse findSalePostDetail(Long userId, Long salePostId) {
        SalePost salePost = salePostRepository.findById(salePostId).orElseThrow(() ->
                new SalePostNotFoundException(salePostId));

        boolean isOpen = salePost.getSaleStatus() == SalePostStatus.OPEN;
        boolean isOwner = salePost.getUser().getId().equals(userId);

        if(!isOpen && !isOwner) {
                throw new SalePostNotFoundException(salePostId);
        }

        List<ProductOption> productOptions = productOptionRepository.findAllBySalePostIdOrderByDisplayOrder(salePostId);
        if(productOptions.isEmpty()){
            throw new InvalidSalePostStateException(salePostId, "등록된 상품 옵션이 없습니다.");
        }

        List<DeliveryMethod> deliveryMethods = deliveryMethodRepository.findAllBySalePostIdOrderByIdDesc(salePostId);
        if(deliveryMethods.isEmpty()){
            throw new InvalidSalePostStateException(salePostId, "등록된 배송 옵션이 없습니다.");
        }

        return SalePostDetailResponse.from(salePost, productOptions, deliveryMethods);
    }

    public Page<MySalePostOverviewResponse> findMySalePosts(
            Long userId,
            String keyword,
            Pageable pageable
    ) {
        return salePostRepository.findByUser_IdAndTitleContaining(userId, keyword, pageable)
                .map(MySalePostOverviewResponse::from);
    }

    @Transactional
    public SalePostStatusChangeResponse changeSalePostStatus(
            Long sellerId,
            Long salePostId,
            SalePostStatus salePostStatus) {
        SalePost salePost = salePostRepository.findByIdAndUser_Id(salePostId, sellerId)
                .orElseThrow(() -> new SalePostNotFoundException(salePostId));

        salePost.changeStatus(salePostStatus);

        return SalePostStatusChangeResponse.from(salePost);
    }

    public MySalePostOverviewResponse findMySalePost(
            Long sellerId,
            Long salePostId) {
        return MySalePostOverviewResponse.from(
            salePostRepository
                    .findByIdAndUser_Id(salePostId, sellerId)
                    .orElseThrow(() -> new SalePostNotFoundException(salePostId))
        );
    }
}
