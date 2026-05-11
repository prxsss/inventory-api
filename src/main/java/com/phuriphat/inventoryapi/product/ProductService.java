package com.phuriphat.inventoryapi.product;

import com.phuriphat.inventoryapi.product.dto.CreateProductRequest;
import com.phuriphat.inventoryapi.product.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    ProductResponse create(CreateProductRequest createProductRequest);

    ProductResponse getById(Long id);

    ProductResponse update(Long id, CreateProductRequest createProductRequest);

    void delete(Long id);

    List<ProductResponse> search(String keyword);

    Page<ProductResponse> search(String keyword, Pageable pageable);
}
