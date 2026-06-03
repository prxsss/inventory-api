package com.phuriphat.inventoryapi.product;

import com.phuriphat.inventoryapi.category.Category;
import com.phuriphat.inventoryapi.category.CategoryRepository;
import com.phuriphat.inventoryapi.exception.DuplicateResourceException;
import com.phuriphat.inventoryapi.exception.ResourceNotFoundException;
import com.phuriphat.inventoryapi.product.dto.CreateProductRequest;
import com.phuriphat.inventoryapi.product.dto.ProductOptionProjection;
import com.phuriphat.inventoryapi.product.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ProductResponse create(CreateProductRequest createProductRequest) {
         Category category = categoryRepository.findById(createProductRequest.getCategoryId())
                 .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

         if (productRepository.existsBySkuIgnoreCase(createProductRequest.getSku())) {
                throw new DuplicateResourceException("Product with sku " + createProductRequest.getSku() + " already exists");
         }

        Product product = Product.builder()
                .sku(createProductRequest.getSku())
                .name(createProductRequest.getName())
                .description(createProductRequest.getDescription())
                .price(createProductRequest.getPrice())
                .price(createProductRequest.getPrice())
                .quantity(createProductRequest.getQuantity())
                .lowStockThreshold(createProductRequest.getLowStockThreshold())
                .category(category)
                .build();

        Product savedProduct = productRepository.save(product);

        return mapToResponse(savedProduct);
    }

    @Override
    public Page<ProductResponse> getAll(String keyword, Pageable pageable) {
        String searchKeyword = (keyword == null || keyword.trim().isEmpty())
                ? ""
                : keyword;

        Page<Product> products = productRepository.findByNameContainingIgnoreCase(searchKeyword, pageable);
        return products.map(this::mapToResponse);
    }

    @Override
    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return mapToResponse(product);
    }

    @Override
    public List<ProductOptionProjection> findAllForOption() {
        return productRepository.findAllProjectedBy();
    }

    @Override
    public ProductResponse update(Long id, CreateProductRequest createProductRequest) {
        Product product =  productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

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
        productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
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
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
