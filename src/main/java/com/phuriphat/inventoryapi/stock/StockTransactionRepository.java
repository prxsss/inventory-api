package com.phuriphat.inventoryapi.stock;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {
    Page<StockTransaction> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);
}
