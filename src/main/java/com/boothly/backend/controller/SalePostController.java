package com.boothly.backend.controller;

import com.boothly.backend.common.response.ApiResponse;
import com.boothly.backend.dto.salepost.SalePostCreateRequest;
import com.boothly.backend.dto.salepost.SalePostListResponse;
import com.boothly.backend.service.SalePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
