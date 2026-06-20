package com.boothly.backend.controller;

import com.boothly.backend.common.response.ApiResponse;
import com.boothly.backend.common.validation.PageRequestValidator;
import com.boothly.backend.dto.PageResponse;
import com.boothly.backend.dto.order.create.OrderCreateRequest;
import com.boothly.backend.dto.order.create.OrderCreateResponse;
import com.boothly.backend.dto.order.list.OrderListResponse;
import com.boothly.backend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/sale-posts/{salePostId}/orders")
    public ResponseEntity<ApiResponse<OrderCreateResponse>> createOrder(
            @RequestParam Long buyerId,
            @PathVariable Long salePostId,
            @Valid @RequestBody OrderCreateRequest request) {
        OrderCreateResponse response = orderService.createOrder(buyerId, salePostId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<PageResponse<OrderListResponse>>> getOrders(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sort
    ){
        PageRequestValidator.validate(page, size);
        Sort sortCondition = switch (sort) {
            case "latest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "oldest" -> Sort.by(Sort.Direction.ASC, "createdAt");
            default -> throw new IllegalArgumentException("지원하지 않는 정렬 옵션입니다.");
        };
        Pageable pageable = PageRequest.of(page, size, sortCondition);
        PageResponse<OrderListResponse> response =
                PageResponse.from(orderService.findMyOrders(userId, keyword, pageable));
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
