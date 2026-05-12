package com.phuriphat.inventoryapi.product;

import com.phuriphat.inventoryapi.product.dto.CreateProductRequest;
import com.phuriphat.inventoryapi.product.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponse create(CreateProductRequest createProductRequest);

    ProductResponse getById(Long id);

    ProductResponse update(Long id, CreateProductRequest createProductRequest);

    void delete(Long id);

    Page<ProductResponse> search(String keyword, Pageable pageable);
}
