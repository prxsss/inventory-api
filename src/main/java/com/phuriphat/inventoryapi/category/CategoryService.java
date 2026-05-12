package com.phuriphat.inventoryapi.category;

import com.phuriphat.inventoryapi.category.dto.CategoryResponse;
import com.phuriphat.inventoryapi.category.dto.CreateCategoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    CategoryResponse create(CreateCategoryRequest createCategoryRequest);

    Page<CategoryResponse> findAll(Pageable pageable);

    CategoryResponse findById(Long id);

    CategoryResponse update(Long id, CreateCategoryRequest createCategoryRequest);

    void delete(Long id);
}
