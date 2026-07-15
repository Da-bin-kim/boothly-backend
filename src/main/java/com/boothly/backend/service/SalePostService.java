package com.boothly.backend.service;

import com.boothly.backend.common.exception.salepost.*;
import com.boothly.backend.common.exception.user.UserNotFoundException;
import com.boothly.backend.domain.enums.OrderStatus;
import com.boothly.backend.domain.enums.SalePostStatus;
import com.boothly.backend.domain.salepost.*;
import com.boothly.backend.domain.user.User;
import com.boothly.backend.dto.salepost.SalePostStatusChangeResponse;
import com.boothly.backend.dto.salepost.create.DeliveryMethodCreateRequest;
import com.boothly.backend.dto.salepost.create.ProductOptionCreateRequest;
import com.boothly.backend.dto.salepost.create.SalePostCreateRequest;
import com.boothly.backend.dto.salepost.create.SalePostCreateResponse;
import com.boothly.backend.dto.salepost.detail.SalePostDetailResponse;
import com.boothly.backend.dto.salepost.list.MySalePostOverviewResponse;
import com.boothly.backend.dto.salepost.list.SalePostListResponse;
import com.boothly.backend.dto.salepost.update.DeliveryMethodUpdateRequest;
import com.boothly.backend.dto.salepost.update.ProductOptionUpdateRequest;
import com.boothly.backend.dto.salepost.update.SalePostUpdateFormResponse;
import com.boothly.backend.dto.salepost.update.SalePostUpdateRequest;
import com.boothly.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalePostService {
    private final SalePostRepository salePostRepository;
    private final UserRepository userRepository;
    private final ProductOptionRepository productOptionRepository;
    private final DeliveryMethodRepository deliveryMethodRepository;
    private final OrderRepository orderRepository;

    private static final List<OrderStatus> ONGOING_ORDER_STATUSES =
            List.of(OrderStatus.REQUESTED, OrderStatus.CONFIRMED);

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

        List<ProductOption> productOptions = productOptionRepository.findAllBySalePost_IdAndDeletedAtIsNullOrderByDisplayOrder(salePostId);
        if(productOptions.isEmpty()){
            throw new InvalidSalePostStateException(salePostId, "등록된 상품 옵션이 없습니다.");
        }

        List<DeliveryMethod> deliveryMethods = deliveryMethodRepository.findAllBySalePost_IdAndActiveTrueOrderByIdDesc(salePostId);
        if(deliveryMethods.isEmpty()){
            throw new InvalidSalePostStateException(salePostId, "등록된 배송 옵션이 없습니다.");
        }

        return SalePostDetailResponse.from(salePost, productOptions, deliveryMethods);
    }

    public Page<MySalePostOverviewResponse> findMySalePosts(
            Long sellerId,
            String keyword,
            Pageable pageable
    ) {
        return salePostRepository.findByUser_IdAndTitleContaining(sellerId, keyword, pageable)
                .map(MySalePostOverviewResponse::from);
    }

    public MySalePostOverviewResponse findMySalePostOverview(
            Long sellerId,
            Long salePostId) {
        return MySalePostOverviewResponse.from(
            salePostRepository
                    .findByIdAndUser_Id(salePostId, sellerId)
                    .orElseThrow(() -> new SalePostNotFoundException(salePostId))
        );
    }

    public SalePostUpdateFormResponse findSalePostForUpdate(
            Long sellerId,
            Long salePostId){
        SalePost salePost = salePostRepository.findByIdAndUser_Id(salePostId, sellerId)
                .orElseThrow(()->new SalePostNotFoundException(salePostId));
        List<DeliveryMethod> deliveryMethods = deliveryMethodRepository.findAllBySalePost_IdAndActiveTrueOrderByIdDesc(salePostId);
        List<ProductOption> productOptions = productOptionRepository.findAllBySalePost_IdAndActiveTrueOrderByDisplayOrder(salePostId);
        boolean hasOrder = orderRepository.existsBySalePost_Id(salePostId);

        return SalePostUpdateFormResponse.from(
                salePost, deliveryMethods, productOptions, hasOrder
        );
    }

    @Transactional
    public SalePostStatusChangeResponse changeSalePostStatus(
            Long sellerId,
            Long salePostId,
            SalePostStatus salePostStatus) {
        SalePost salePost = salePostRepository.findByIdAndUser_Id(salePostId, sellerId)
                .orElseThrow(() -> new SalePostNotFoundException(salePostId));

        if(salePost.getSaleStatus() == SalePostStatus.CLOSED
                && salePostStatus == SalePostStatus.HIDDEN){
            validateNoOngoingOrders(salePostId);
        }
        salePost.changeStatus(salePostStatus);

        return SalePostStatusChangeResponse.from(salePost);
    }

    //TODO 판매글 수정 기능
    @Transactional
    public void updateSalePostDetail(
            Long sellerId,
            Long salePostId,
            SalePostUpdateRequest updateRequest){

        SalePost salePost = salePostRepository.findForUpdateByIdAndUserId(salePostId, sellerId)
                .orElseThrow(()->new SalePostNotFoundException(salePostId));

        boolean hasExistingOrders = orderRepository.existsBySalePost_Id(salePostId);

        List<ProductOptionUpdateRequest> productOptionRequests =
                updateRequest.getProductOptions();

        List<Long> productOptionIds = updateRequest.getProductOptions().stream()
                .map(ProductOptionUpdateRequest::getId)
                .toList();

        validateProductOptionIds(productOptionIds);
        updateProductOptions(salePostId, productOptionRequests, productOptionIds, hasExistingOrders);

    }

    private void validateSalePeriod(SalePostCreateRequest request) {
        if (request.getEndAt() != null
                && request.getEndAt().isBefore(request.getStartAt())) {
            throw new IllegalArgumentException(
                    "판매 종료 시각은 판매 시작 시각보다 빠를 수 없습니다."
            );
        }
    }

    private void validateNoOngoingOrders(Long salePostId){
        boolean hasOngoingOrders = orderRepository.existsBySalePost_IdAndOrderStatusIn(salePostId, ONGOING_ORDER_STATUSES);

        if (hasOngoingOrders) {
            throw new SalePostHasOngoingOrdersException(salePostId);
        }
    }
    private void validateProductOptionIds(List<Long> productOptionIds) {
        Set<Long> uniqueIds = new HashSet<>(productOptionIds);
        if(productOptionIds.size() != uniqueIds.size()) {
            throw new DuplicateProductOptionException();
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
                        .active(true)
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
                        .active(true)
                        .build())
                .toList();

        deliveryMethodRepository.saveAll(deliveryMethods);
    }

    private void updateProductOptions(Long salePostId, List<ProductOptionUpdateRequest> updateRequests, List<Long> productOptionIds, boolean hasExistingOrders) {
        Map<Long, ProductOption> productOptions = getProductOptionsByIds(salePostId, productOptionIds);

        updateRequests.forEach(request -> {
            ProductOption productOption = productOptions.get(request.getId());
            productOption.update(request.toUpdateValues(), hasExistingOrders);
        });
    }

    private Map<Long,DeliveryMethod> getDeliveryMethodsByIds(Long salePostId, List<Long> deliveryMethodIds){
        List<DeliveryMethod> deliveryMethods = deliveryMethodRepository
                .findAllBySalePost_IdAndActiveTrueAndIdIn(salePostId, deliveryMethodIds);
        if (deliveryMethodIds.size() != deliveryMethods.size()) {
            throw new DeliveryMethodNotFoundException();
        }
        return deliveryMethods.stream()
                .collect(Collectors.toMap(DeliveryMethod::getId, (deliveryMethod) -> deliveryMethod));
    }

    private Map<Long,ProductOption> getProductOptionsByIds(Long salePostId, List<Long> productOptionIds) {
        List<ProductOption> productOptions = productOptionRepository
                .findAllBySalePost_IdAndDeletedAtIsNullAndIdIn(salePostId, productOptionIds);
        if(productOptionIds.size() != productOptions.size()) {
            throw new ProductOptionNotFoundException();
        }
        return productOptions.stream()
                .collect(Collectors.toMap(ProductOption::getId, productOption->productOption));
    }
}
