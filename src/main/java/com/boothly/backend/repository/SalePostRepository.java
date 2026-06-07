package com.boothly.backend.repository;

import com.boothly.backend.domain.salepost.SalePost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalePostRepository extends JpaRepository<SalePost, Long> {
}
