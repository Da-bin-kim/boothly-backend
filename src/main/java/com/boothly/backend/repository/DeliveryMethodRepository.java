package com.boothly.backend.repository;

import com.boothly.backend.domain.salepost.DeliveryMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryMethodRepository extends JpaRepository<DeliveryMethod, Long> {
}
