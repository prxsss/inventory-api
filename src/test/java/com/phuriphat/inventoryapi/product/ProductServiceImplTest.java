package com.phuriphat.inventoryapi.product;

import com.phuriphat.inventoryapi.category.Category;
import com.phuriphat.inventoryapi.category.CategoryRepository;
import com.phuriphat.inventoryapi.exception.DuplicateResourceException;
import com.phuriphat.inventoryapi.exception.ResourceNotFoundException;
import com.phuriphat.inventoryapi.product.dto.CreateProductRequest;
import com.phuriphat.inventoryapi.product.dto.ProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private final Long categoryId = 1L;
    private final String sku = "SKU-001";
    private final String name = "Test Product";
    private final String description = "Test Description";
    private final BigDecimal price = new BigDecimal("100.00");
    private final Integer quantity = 10;
    private final Integer lowStockThreshold = 5;

    private Category createCategory() {
        return Category.builder()
                .id(categoryId)
                .name("Electronics")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private CreateProductRequest createProductRequest() {
        CreateProductRequest request = new CreateProductRequest();
        request.setSku(sku);
        request.setName(name);
        request.setDescription(description);
        request.setPrice(price);
        request.setQuantity(quantity);
        request.setLowStockThreshold(lowStockThreshold);
        request.setCategoryId(categoryId);
        return request;
    }

    private Product createProduct(Long id, Category category) {
        return Product.builder()
                .id(id)
                .sku(sku)
                .name(name)
                .description(description)
                .price(price)
                .quantity(quantity)
                .lowStockThreshold(lowStockThreshold)
                .category(category)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("create should return saved product response when SKU is unique and category exists")
    void create_withValidRequest_shouldReturnSavedProductResponse() {
        Category category = createCategory();
        CreateProductRequest request = createProductRequest();
        Product savedProduct = createProduct(1L, category);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(productRepository.existsBySkuIgnoreCase(sku)).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductResponse response = productService.create(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(sku, response.getSku());
        assertEquals(name, response.getName());
        assertEquals(description, response.getDescription());
        assertEquals(price, response.getPrice());
        assertEquals(quantity, response.getQuantity());
        assertEquals(lowStockThreshold, response.getLowStockThreshold());
        assertEquals("Electronics", response.getCategoryName());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("create should throw DuplicateResourceException when SKU already exists")
    void create_withDuplicateSku_shouldThrowDuplicateResourceException() {
        Category category = createCategory();
        CreateProductRequest request = createProductRequest();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(productRepository.existsBySkuIgnoreCase(sku)).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> productService.create(request));

        assertEquals("Product with sku " + sku + " already exists", exception.getMessage());

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("create should throw ResourceNotFoundException when category does not exist")
    void create_withNonExistentCategory_shouldThrowResourceNotFoundException() {
        CreateProductRequest request = createProductRequest();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.create(request));

        assertEquals("Category not found", exception.getMessage());

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("getAll should return matching products page when keyword is provided")
    void getAll_withKeyword_shouldReturnMatchingProductsPage() {
        String keyword = "test";
        Pageable pageable = PageRequest.of(0, 10);
        Category category = createCategory();
        List<Product> products = List.of(createProduct(1L, category), createProduct(2L, category));
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

        when(productRepository.findByNameContainingIgnoreCase(keyword, pageable)).thenReturn(productPage);

        Page<ProductResponse> result = productService.getAll(keyword, pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("Electronics", result.getContent().get(0).getCategoryName());
        assertEquals("Electronics", result.getContent().get(1).getCategoryName());

        verify(productRepository, times(1)).findByNameContainingIgnoreCase(keyword, pageable);
    }

    @Test
    @DisplayName("getAll should search with empty string when keyword is null")
    void getAll_withNullKeyword_shouldSearchWithEmptyString() {
        Pageable pageable = PageRequest.of(0, 10);
        Category category = createCategory();
        List<Product> products = List.of(createProduct(1L, category));
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

        when(productRepository.findByNameContainingIgnoreCase("", pageable)).thenReturn(productPage);

        Page<ProductResponse> result = productService.getAll(null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(productRepository, times(1)).findByNameContainingIgnoreCase("", pageable);
    }

    @Test
    @DisplayName("getById should return product response when ID exists")
    void getById_withExistingId_shouldReturnProductResponse() {
        Category category = createCategory();
        Product product = createProduct(1L, category);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse response = productService.getById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(sku, response.getSku());
        assertEquals(name, response.getName());
        assertEquals(price, response.getPrice());
        assertEquals(quantity, response.getQuantity());
        assertEquals("Electronics", response.getCategoryName());

        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getById should throw ResourceNotFoundException when ID does not exist")
    void getById_withNonExistentId_shouldThrowResourceNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.getById(99L));

        assertEquals("Product not found", exception.getMessage());

        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("update should return updated product response when request is valid")
    void update_withValidRequest_shouldReturnUpdatedProductResponse() {
        Category category = createCategory();
        Product existingProduct = createProduct(1L, category);
        CreateProductRequest request = createProductRequest();
        request.setSku("SKU-UPDATED");
        request.setName("Updated Name");

        Product updatedProduct = createProduct(1L, category);
        updatedProduct.setSku("SKU-UPDATED");
        updatedProduct.setName("Updated Name");

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        ProductResponse response = productService.update(1L, request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("SKU-UPDATED", response.getSku());
        assertEquals("Updated Name", response.getName());

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(existingProduct);
    }

    @Test
    @DisplayName("update should throw ResourceNotFoundException when product does not exist")
    void update_withNonExistentId_shouldThrowResourceNotFoundException() {
        CreateProductRequest request = createProductRequest();

        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.update(99L, request));

        assertEquals("Product not found", exception.getMessage());

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("delete should remove product when ID exists")
    void delete_withExistingId_shouldDeleteSuccessfully() {
        Category category = createCategory();
        Product product = createProduct(1L, category);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).deleteById(1L);

        productService.delete(1L);

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("delete should throw ResourceNotFoundException when product does not exist")
    void delete_withNonExistentId_shouldThrowResourceNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.delete(99L));

        assertEquals("Product not found", exception.getMessage());

        verify(productRepository, never()).deleteById(any());
    }
}
