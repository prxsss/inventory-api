package com.phuriphat.inventoryapi.category;

import com.phuriphat.inventoryapi.category.dto.CategoryOptionProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Page<Category> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    List<CategoryOptionProjection> findAllProjectedBy();

    boolean existsByNameIgnoreCase(String name);
}
