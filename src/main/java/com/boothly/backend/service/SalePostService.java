package com.boothly.backend.service;

import com.boothly.backend.common.exception.ErrorCode;
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
import com.boothly.backend.dto.salepost.update.*;
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
     * @param createRequest 판매글 생성 요청 DTO
     * @return 생성된 판매글 ID
     */
    @Transactional
    public SalePostCreateResponse createSalePost(Long sellerId, SalePostCreateRequest createRequest) {
        /* 유효성 검증 */
        User seller = userRepository.findById(sellerId).orElseThrow(() ->
                new UserNotFoundException(sellerId));

        SalePost salePost = SalePost.create(seller, createRequest.toCreateValues());

        /* DB 저장 */
        SalePost savedSalePost = salePostRepository.save(salePost);
        saveProductOptions(savedSalePost, createRequest.getProductOptions());
        saveDeliveryMethods(savedSalePost, createRequest.getDeliveryMethods());

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

        List<DeliveryMethod> deliveryMethods = deliveryMethodRepository.findAllBySalePost_IdAndDeletedAtIsNullOrderByIdDesc(salePostId);
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
        List<DeliveryMethod> deliveryMethods = deliveryMethodRepository.findAllBySalePost_IdAndDeletedAtIsNullOrderByIdDesc(salePostId);
        List<ProductOption> productOptions = productOptionRepository.findAllBySalePost_IdAndDeletedAtIsNullOrderByDisplayOrder(salePostId);
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
        SalePost salePost = salePostRepository.findForUpdateByIdAndUserId(salePostId, sellerId)
                .orElseThrow(() -> new SalePostNotFoundException(salePostId));

        if(salePost.getSaleStatus() == SalePostStatus.CLOSED
                && salePostStatus == SalePostStatus.HIDDEN){
            validateNoOngoingOrders(salePostId);
        }
        salePost.changeStatus(salePostStatus);

        return SalePostStatusChangeResponse.from(salePost);
    }

    @Transactional
    public SalePostUpdateResponse updateSalePostDetail(
            Long sellerId,
            Long salePostId,
            SalePostUpdateRequest updateRequest){

        SalePost salePost = salePostRepository.findForUpdateByIdAndUserId(salePostId, sellerId)
                .orElseThrow(()->new SalePostNotFoundException(salePostId));
        salePost.update(updateRequest.toUpdateValues());


        boolean hasExistingOrders = orderRepository.existsBySalePost_Id(salePostId);

        List<ProductOptionUpdateRequest> productOptionRequests =
                updateRequest.getProductOptions();

        addNewProductOptions(salePost, productOptionRequests);
        updateExistingProductOptions(salePostId, productOptionRequests, hasExistingOrders);

        validateAtLeastOneActiveProductOption(salePostId);

        List<DeliveryMethodUpdateRequest> deliveryMethodUpdateRequests =
                updateRequest.getDeliveryMethods();

        addNewDeliveryMethods(salePost, deliveryMethodUpdateRequests);
        updateExistingDeliveryMethods(salePostId, deliveryMethodUpdateRequests, hasExistingOrders);

        validateAtLeastOneActiveDeliveryMethod(salePostId);

        return new SalePostUpdateResponse(salePostId);
    }



    private void validateNoOngoingOrders(Long salePostId){
        boolean hasOngoingOrders = orderRepository.existsBySalePost_IdAndOrderStatusIn(salePostId, ONGOING_ORDER_STATUSES);

        if (hasOngoingOrders) {
            throw new SalePostHasOngoingOrdersException(salePostId);
        }
    }

    private void validateNoDuplicateProductOptionIds(List<Long> productOptionIds) {
        Set<Long> uniqueIds = new HashSet<>(productOptionIds);
        if(productOptionIds.size() != uniqueIds.size()) {
            throw new DuplicateProductOptionException();
        }
    }

    private void validateNoDuplicateDeliveryMethodIds(List<Long> deliveryMethodIds) {
        Set<Long> uniqueIds = new HashSet<>(deliveryMethodIds);
        if (deliveryMethodIds.size() != uniqueIds.size()) {
            throw new DuplicateDeliveryMethodIdException();
        }
    }

    private void validateAtLeastOneActiveProductOption(Long salePostId) {
        if (!productOptionRepository.existsBySalePost_IdAndDeletedAtIsNullAndActiveIsTrue(salePostId)){
            throw new ProductOptionException (
                    ErrorCode.PRODUCT_OPTION_AT_LEAST_ONE_ACTIVE_REQUIRED,
                    salePostId
            );
        }
    }

    private void validateAtLeastOneActiveDeliveryMethod(Long salePostId) {
        if (!deliveryMethodRepository.existsBySalePost_IdAndDeletedAtIsNullAndActiveIsTrue(salePostId)){
            throw new DeliveryMethodException (
                    ErrorCode.DELIVERY_METHOD_AT_LEAST_ONE_ACTIVE_REQUIRED,
                    salePostId
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

    private void addNewProductOptions(
            SalePost salePost,
            List<ProductOptionUpdateRequest> requestedProductOptions
    ) {
        List<ProductOptionCreateRequest> productOptionCreateRequests = requestedProductOptions.stream()
                .filter(ProductOptionUpdateRequest::isCreateRequest)
                .map(ProductOptionUpdateRequest::toCreateRequest)
                .toList();
        saveProductOptions(salePost, productOptionCreateRequests);
    }

    private void addNewDeliveryMethods(
            SalePost salePost,
            List<DeliveryMethodUpdateRequest> requestedDeliveryMethods
    ) {
        List<DeliveryMethodCreateRequest> deliveryMethodCreateRequests =
                requestedDeliveryMethods.stream()
                        .filter(DeliveryMethodUpdateRequest::isCreateRequest)
                        .map(DeliveryMethodUpdateRequest::toCreateRequest)
                        .toList();

        saveDeliveryMethods(salePost, deliveryMethodCreateRequests);
    }

    private void updateExistingProductOptions(
            Long salePostId,
            List<ProductOptionUpdateRequest> requestedProductOptions,
            boolean hasExistingOrders
    ) {
        List<ProductOptionUpdateRequest> existingProductOptionRequests =
                requestedProductOptions.stream()
                        .filter(request -> !request.isCreateRequest())
                        .toList();

        List<Long> existingProductOptionIds = existingProductOptionRequests.stream()
                .map(ProductOptionUpdateRequest::getId)
                .toList();

        validateNoDuplicateProductOptionIds(existingProductOptionIds);

        Map<Long, ProductOption> productOptions = getProductOptionsByIds(salePostId, existingProductOptionIds);

        existingProductOptionRequests.forEach(request -> {
            ProductOption productOption = productOptions.get(request.getId());
            productOption.update(request.toUpdateValues(), hasExistingOrders);
        });
    }

    private void updateExistingDeliveryMethods(
            Long salePostId,
            List<DeliveryMethodUpdateRequest> requestedDeliveryMethods,
            boolean hasExistingOrders
    ) {
        List<DeliveryMethodUpdateRequest> existingDeliveryMethodRequests =
                requestedDeliveryMethods.stream()
                        .filter(request -> !request.isCreateRequest())
                        .toList();

        List<Long> existingDeliveryMethodIds = existingDeliveryMethodRequests.stream()
                .map(DeliveryMethodUpdateRequest::getId)
                .toList();

        validateNoDuplicateDeliveryMethodIds(existingDeliveryMethodIds);

        Map<Long, DeliveryMethod> deliveryMethods = getDeliveryMethodsByIds(salePostId, existingDeliveryMethodIds);

        existingDeliveryMethodRequests.forEach(request -> {
            DeliveryMethod deliveryMethod = deliveryMethods.get(request.getId());
            deliveryMethod.update(request.toUpdateValues(), hasExistingOrders);
        });
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

    private Map<Long,DeliveryMethod> getDeliveryMethodsByIds(Long salePostId, List<Long> deliveryMethodIds){
        List<DeliveryMethod> deliveryMethods = deliveryMethodRepository
                .findAllBySalePost_IdAndDeletedAtIsNullAndIdIn(salePostId, deliveryMethodIds);
        if (deliveryMethodIds.size() != deliveryMethods.size()) {
            throw new DeliveryMethodNotFoundException();
        }
        return deliveryMethods.stream()
                .collect(Collectors.toMap(DeliveryMethod::getId, (deliveryMethod) -> deliveryMethod));
    }
}
