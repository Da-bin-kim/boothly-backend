package com.boothly.backend.repository;

import com.boothly.backend.domain.enums.SalePostStatus;
import com.boothly.backend.domain.salepost.SalePost;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Optional;


public interface SalePostRepository extends JpaRepository<SalePost, Long> {
    @EntityGraph(attributePaths = "user")
    Page<SalePost> findBySaleStatusAndTitleContaining(SalePostStatus status, String keyword, Pageable pageable);

    Page<SalePost> findByUser_IdAndTitleContaining(Long sellerId, String keyword, Pageable pageable);

    boolean existsByIdAndUser_Id(Long salePostId, Long userId);

    Optional<SalePost> findByIdAndUser_Id(Long salePostId, Long sellerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
    Optional<SalePost> findForUpdateByIdAndUserId(Long id, Long sellerId);
}
