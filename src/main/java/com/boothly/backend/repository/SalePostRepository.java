package com.boothly.backend.repository;

import com.boothly.backend.domain.enums.SalePostStatus;
import com.boothly.backend.domain.salepost.SalePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface SalePostRepository extends JpaRepository<SalePost, Long> {
    @EntityGraph(attributePaths = "user")
    Page<SalePost> findBySaleStatusAndTitleContaining(SalePostStatus status, String keyword, Pageable pageable);

    Page<SalePost> findByUser_IdAndTitleContaining(Long userId, String keyword, Pageable pageable);

    boolean existsByIdAndUser_Id(Long salePostId, Long userId);

    Optional<SalePost> findByIdAndUser_Id(Long salePostId, Long sellerId);
}
