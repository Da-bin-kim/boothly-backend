package com.boothly.backend.repository;

import com.boothly.backend.domain.salepost.DeliveryMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryMethodRepository extends JpaRepository<DeliveryMethod, Long> {
    List<DeliveryMethod> findAllBySalePostIdOrderByIdDesc(Long salePostId);
}
