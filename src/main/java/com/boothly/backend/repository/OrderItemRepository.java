package com.boothly.backend.repository;

import com.boothly.backend.domain.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder_id(Long orderId);
    List<OrderItem> findByOrder_idIn(List<Long> orderIds);
}
