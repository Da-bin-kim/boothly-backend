package com.boothly.backend.controller;

import com.boothly.backend.common.response.ApiResponse;
import com.boothly.backend.common.validation.PageRequestValidator;
import com.boothly.backend.dto.PageResponse;
import com.boothly.backend.dto.salepost.SalePostCloseResponse;
import com.boothly.backend.dto.salepost.list.MySalePostListResponse;
import com.boothly.backend.dto.salepost.create.SalePostCreateRequest;
import com.boothly.backend.dto.salepost.detail.SalePostDetailResponse;
import com.boothly.backend.dto.salepost.list.SalePostListResponse;
import com.boothly.backend.service.SalePostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SalePostController {
    private final SalePostService salePostService;

    @PostMapping("/sale-posts")
    public ResponseEntity<ApiResponse<Long>> create(
            @RequestParam Long sellerId,
            @Valid @RequestBody SalePostCreateRequest request
    ) {
        Long salePostId = salePostService.createSalePost(sellerId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(salePostId));
    }

    @GetMapping("/sale-posts")
    public ResponseEntity<ApiResponse<PageResponse<SalePostListResponse>>> getSalePosts(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sort
    ){
        PageRequestValidator.validate(page, size);

        Sort sortCondition = getSalePostSortCondition(sort);
        Pageable pageable = PageRequest.of(page, size, sortCondition);

        Page<SalePostListResponse> response = salePostService.findOpenSalePosts(keyword, pageable);

        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(response)));
    }

    private Sort getSalePostSortCondition(String sort) {
        return switch (sort) {
            case "latest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "endingSoon" -> Sort.by(Sort.Direction.ASC, "endAt");
            default -> throw new IllegalArgumentException("지원하지 않는 정렬 옵션입니다.");
        };
    }

    @GetMapping("/sale-posts/{salePostId}")
    public ResponseEntity<ApiResponse<SalePostDetailResponse>> getDetail(
            @PathVariable Long salePostId
    ){
        return ResponseEntity.ok(ApiResponse.success(salePostService.findSalePostDetail(salePostId)));
    }

    @GetMapping("/seller/sale-posts")
    public ResponseEntity<ApiResponse<PageResponse<MySalePostListResponse>>> getMySalePosts(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sort
    ){
        PageRequestValidator.validate(page, size);
        Sort sortCondition = getSalePostSortCondition(sort);
        Pageable pageable = PageRequest.of(page, size, sortCondition);

        PageResponse<MySalePostListResponse> response = PageResponse.from(salePostService.findMySalePosts(userId, keyword, pageable));

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/seller/sale-posts/{salePostId}/close")
    public ResponseEntity<ApiResponse<SalePostCloseResponse>> closeSalePost(
            @RequestParam Long sellerId,
            @PathVariable Long salePostId
    ){
        return ResponseEntity.ok(ApiResponse.success(
                salePostService.closeSalePost(sellerId, salePostId))
        );
    }
}
