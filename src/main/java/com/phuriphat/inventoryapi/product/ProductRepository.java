package com.phuriphat.inventoryapi.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
        SELECT p
        FROM Product p
        WHERE p.quantity <= p.lowStockThreshold
    """)
    List<Product> findLowStockProducts();

    @Query("""
        SELECT COUNT(p)
        FROM Product p
        WHERE p.quantity <= p.lowStockThreshold
    """)
    long countLowStockProducts();

    @Query("""
        SELECT COALESCE(SUM(p.quantity), 0)
        FROM Product p
    """)
    Integer getTotalQuantity();

    List<Product> findByNameContainingIgnoreCase(String keyword);

    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}
