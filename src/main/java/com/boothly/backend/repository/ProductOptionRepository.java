package com.boothly.backend.repository;

import com.boothly.backend.domain.salepost.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    List<ProductOption> findAllBySalePostIdOrderByDisplayOrder(Long salePostId);
}
