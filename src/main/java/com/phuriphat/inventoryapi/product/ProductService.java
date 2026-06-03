package com.phuriphat.inventoryapi.product;

import com.phuriphat.inventoryapi.product.dto.CreateProductRequest;
import com.phuriphat.inventoryapi.product.dto.ProductOptionProjection;
import com.phuriphat.inventoryapi.product.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    ProductResponse create(CreateProductRequest createProductRequest);

    Page<ProductResponse> getAll(String keyword, Pageable pageable);

    ProductResponse getById(Long id);

    List<ProductOptionProjection> findAllForOption();

    ProductResponse update(Long id, CreateProductRequest createProductRequest);

    void delete(Long id);
}
