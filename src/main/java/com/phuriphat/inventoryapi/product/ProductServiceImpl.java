package com.phuriphat.inventoryapi.product;

import com.phuriphat.inventoryapi.product.dto.CreateProductRequest;
import com.phuriphat.inventoryapi.product.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ProductResponse create(CreateProductRequest createProductRequest) {
        Product product = Product.builder()
                .sku(createProductRequest.getSku())
                .name(createProductRequest.getName())
                .description(createProductRequest.getDescription())
                .price(createProductRequest.getPrice())
                .price(createProductRequest.getPrice())
                .quantity(createProductRequest.getQuantity())
                .lowStockThreshold(createProductRequest.getLowStockThreshold())
                .build();

        Product savedProduct = productRepository.save(product);

        return mapToResponse(savedProduct);
    }

    @Override
    public List<ProductResponse> getAll() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return mapToResponse(product);
    }

    @Override
    public ProductResponse update(Long id, CreateProductRequest createProductRequest) {
        Product product =  productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setSku(createProductRequest.getSku());
        product.setName(createProductRequest.getName());
        product.setDescription(createProductRequest.getDescription());
        product.setPrice(createProductRequest.getPrice());
        product.setQuantity(createProductRequest.getQuantity());
        product.setLowStockThreshold(createProductRequest.getLowStockThreshold());

        Product updatedProduct = productRepository.save(product);

        return mapToResponse(updatedProduct);
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description((product.getDescription()))
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .lowStockThreshold(product.getLowStockThreshold())
                .createdAt(product.getCreatedAt())
                .build();
    }
}
