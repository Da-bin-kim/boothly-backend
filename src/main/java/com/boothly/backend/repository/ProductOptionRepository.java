package com.boothly.backend.repository;

import com.boothly.backend.domain.salepost.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    List<ProductOption> findAllBySalePostIdOrderByDisplayOrder(Long salePostId);
    List<ProductOption> findAllBySalePost_IdAndIdIn(Long salePostId, Collection<Long> productOptionIds);
}
