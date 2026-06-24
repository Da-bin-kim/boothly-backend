package com.boothly.backend.repository;

import com.boothly.backend.domain.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByOrderNumber(String orderNumber);

    @EntityGraph(attributePaths = "salePost")
    Page<Order> findByBuyer_IdAndSalePost_TitleContaining(Long userId, String title, Pageable pageable);

    Optional<Order> findByIdAndBuyer_Id(Long orderId, Long userId);

    Page<Order> findBySalePost_Id(Long salePostId, Pageable pageable);

    @EntityGraph(attributePaths = "salePost")
    Optional<Order> findBySalePost_User_IdAndSalePost_idAndId(Long sellerId, Long salePostId, Long orderId);

}
