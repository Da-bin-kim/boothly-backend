package com.boothly.backend.controller;

import com.boothly.backend.common.response.ApiResponse;
import com.boothly.backend.dto.order.OrderCreateRequest;
import com.boothly.backend.dto.order.OrderCreateResponse;
import com.boothly.backend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
}
