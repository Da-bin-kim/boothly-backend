package com.boothly.backend.controller;

import com.boothly.backend.common.response.ApiResponse;
import com.boothly.backend.dto.salepost.SalePostCreateRequest;
import com.boothly.backend.dto.salepost.SalePostListResponse;
import com.boothly.backend.service.SalePostService;
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
@RequestMapping("/api/sale-posts")
public class SalePostController {
    private final SalePostService salePostService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> create(
            @RequestParam Long sellerId,
            @RequestBody SalePostCreateRequest request
    ) {
        Long salePostId = salePostService.create(sellerId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(salePostId));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SalePostListResponse>>> findAll(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sort
    ){
        validatePageRequest(page, size);

        Sort sortCondition = getSortCondition(sort);
        Pageable pageable = PageRequest.of(page, size, sortCondition);

        Page<SalePostListResponse> response = salePostService.findAll(keyword, pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }


    private void validatePageRequest(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("page는 0 이상이어야 합니다.");
        }

        if (size < 1 || size > 50) {
            throw new IllegalArgumentException("size는 1 이상 50 이하이어야 합니다.");
        }
    }

    private Sort getSortCondition(String sort) {
        return switch (sort) {
            case "latest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "endingSoon" -> Sort.by(Sort.Direction.ASC, "endAt");
            default -> throw new IllegalArgumentException("지원하지 않는 정렬 옵션입니다.");
        };
    }
}
