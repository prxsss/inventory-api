package com.phuriphat.inventoryapi.category;

import com.phuriphat.inventoryapi.category.dto.CategoryOptionProjection;
import com.phuriphat.inventoryapi.category.dto.CategoryResponse;
import com.phuriphat.inventoryapi.category.dto.CreateCategoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    CategoryResponse create(CreateCategoryRequest createCategoryRequest);

    Page<CategoryResponse> findAll(String keyword, Pageable pageable);

    CategoryResponse findById(Long id);

    List<CategoryOptionProjection> findAllForOption();

    CategoryResponse update(Long id, CreateCategoryRequest createCategoryRequest);

    void delete(Long id);
}
