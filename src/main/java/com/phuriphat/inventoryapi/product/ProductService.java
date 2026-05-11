package com.phuriphat.inventoryapi.product;

import com.phuriphat.inventoryapi.product.dto.CreateProductRequest;
import com.phuriphat.inventoryapi.product.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse create(CreateProductRequest createProductRequest);

    ProductResponse getById(Long id);

    ProductResponse update(Long id, CreateProductRequest createProductRequest);

    void delete(Long id);

    List<ProductResponse> search(String keyword);
}
