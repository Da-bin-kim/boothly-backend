package com.boothly.backend.repository;

import com.boothly.backend.domain.salepost.DeliveryMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryMethodRepository extends JpaRepository<DeliveryMethod, Long> {
    List<DeliveryMethod> findAllBySalePost_IdAndActiveTrueOrderByIdDesc(Long salePostId);
    Optional<DeliveryMethod> findBySalePost_IdAndId(Long salePostId, Long deliveryMethodId);
}
