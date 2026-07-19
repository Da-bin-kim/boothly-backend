package com.boothly.backend.repository;

import com.boothly.backend.domain.salepost.DeliveryMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DeliveryMethodRepository extends JpaRepository<DeliveryMethod, Long> {
    List<DeliveryMethod> findAllBySalePost_IdAndDeletedAtIsNullOrderByIdDesc(Long salePostId);
    Optional<DeliveryMethod> findBySalePost_IdAndDeletedAtIsNullAndId(Long salePostId, Long deliveryMethodId);
    List<DeliveryMethod> findAllBySalePost_IdAndDeletedAtIsNullAndIdIn(Long salePostId, Collection<Long> deliveryMethodIds);
}
