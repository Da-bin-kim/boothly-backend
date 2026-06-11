package com.boothly.backend.repository;

import com.boothly.backend.domain.enums.SalePostStatus;
import com.boothly.backend.domain.salepost.SalePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SalePostRepository extends JpaRepository<SalePost, Long> {
    @EntityGraph(attributePaths = "user")
    Page<SalePost> findBysaleStatusAndTitleContaining(SalePostStatus status, String keyword, Pageable pageable);

}
