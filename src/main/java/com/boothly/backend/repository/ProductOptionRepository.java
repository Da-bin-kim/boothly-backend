package com.boothly.backend.repository;

import com.boothly.backend.domain.salepost.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    List<ProductOption> findAllBySalePost_IdAndActiveTrueOrderByDisplayOrder(Long salePostId);
    List<ProductOption> findAllBySalePost_IdAndIdIn(Long salePostId, Collection<Long> productOptionIds);

    @Modifying(flushAutomatically = true)
    @Query("""
        update ProductOption po
        set po.stockQuantity = po.stockQuantity - :quantity
        where po.id = :productOptionId
          and po.salePost.id = :salePostId
          and po.active = true
          and po.stockQuantity >= :quantity
    """)
    int decreaseStock(Long salePostId, Long productOptionId, int quantity);

    @Modifying
    @Query("""
        update ProductOption po
        set po.stockQuantity = po.stockQuantity + :restoreQuantity
        where po.id = :productOptionId
          and po.salePost.id = :salePostId
    """)
    int restoreStock(Long salePostId, Long productOptionId, int restoreQuantity);
}
