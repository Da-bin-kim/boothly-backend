package com.boothly.backend.repository;

import com.boothly.backend.domain.enums.OrderStatus;
import com.boothly.backend.domain.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByOrderNumber(String orderNumber);

    @EntityGraph(attributePaths = "salePost")
    Page<Order> findByBuyer_IdAndSalePost_TitleContaining(Long userId, String title, Pageable pageable);

    Optional<Order> findByIdAndBuyer_Id(Long orderId, Long userId);

    Page<Order> findBySalePost_Id(Long salePostId, Pageable pageable);

    @EntityGraph(attributePaths = "salePost")
    Optional<Order> findBySalePost_User_IdAndSalePost_idAndId(Long sellerId, Long salePostId, Long orderId);

    Optional<Order> findByBuyer_idAndId(Long buyerId, Long id);

    @Modifying(clearAutomatically = true)
    @Query("""
           update Order o
           set o.orderStatus = com.boothly.backend.domain.enums.OrderStatus.CANCELED
           where o.id = :orderId
             and o.buyer.id = :buyerId
             and o.orderStatus = com.boothly.backend.domain.enums.OrderStatus.REQUESTED
       """)
    int cancelRequestedOrderByBuyer(Long buyerId, Long orderId);

    @Modifying(clearAutomatically = true)
    @Query("""
        update Order o
        set o.orderStatus = :targetOrderStatus
        where o.id = :orderId
          and o.salePost.id = :salePostId
          and o.salePost.user.id = :sellerId
          and o.orderStatus = :expectedCurrentStatus
    """)
    int changeOrderStatusBySeller(Long sellerId, Long salePostId, Long orderId, OrderStatus expectedCurrentStatus, OrderStatus targetOrderStatus);

    boolean existsBySalePost_IdAndOrderStatusIn(
            Long salePostId,
            List <OrderStatus> orderStatusList
    );
}
