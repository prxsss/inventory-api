package com.phuriphat.inventoryapi.stock;

import com.phuriphat.inventoryapi.category.Category;
import com.phuriphat.inventoryapi.exception.ResourceNotFoundException;
import com.phuriphat.inventoryapi.product.Product;
import com.phuriphat.inventoryapi.product.ProductRepository;
import com.phuriphat.inventoryapi.stock.dto.StockHistoryResponse;
import com.phuriphat.inventoryapi.stock.dto.StockRequest;
import com.phuriphat.inventoryapi.stock.dto.StockTransactionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceImplTest {

    @Mock
    private ProductRepository  productRepository;

    @Mock
    private StockTransactionRepository stockTransactionRepository;

    @InjectMocks
    private StockServiceImpl stockService;

    private Category createCategory() {
        return Category.builder()
                .id(1L)
                .name("Electronics")
                .build();
    }

    private Product createProduct() {
        return createProduct(1L);
    }

    private Product createProduct(Long id) {
        return Product.builder()
                .id(id)
                .sku("SKU" + id)
                .name("Product " + id)
                .description("Description for product " + id)
                .price(BigDecimal.valueOf(100.00))
                .quantity(10)
                .lowStockThreshold(5)
                .category(createCategory())
                .build();
    }

    private StockRequest createStockRequest(Long id) {
        StockRequest request = new StockRequest();
        request.setProductId(id);
        request.setQuantity(1);
        request.setNote("Restock from supplier");
        return request;
    }

    private StockTransaction createStockTransaction(Long id) {
        return StockTransaction.builder()
                .id(id)
                .product(createProduct(id))
                .type(TransactionType.IN)
                .quantity(1)
                .note("Restock from supplier")
                .build();
    }

    @Test
    @DisplayName("stockIn should increase quantity and persist IN transaction")
    void stockIn_withValidRequest_shouldIncreaseQuantityAndSaveTransaction() {
        // Given
        Product existingProduct = createProduct();
        StockRequest request = createStockRequest(1L);
        int originalQuantity = existingProduct.getQuantity();

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(stockTransactionRepository.save(any(StockTransaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        stockService.stockIn(request);

        // Then
        ArgumentCaptor<StockTransaction> transactionCaptor = ArgumentCaptor.forClass(StockTransaction.class);
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        verify(productRepository, times(1)).findById(1L);
        verify(stockTransactionRepository, times(1)).save(transactionCaptor.capture());
        verify(productRepository, times(1)).save(productCaptor.capture());

        StockTransaction savedTransaction = transactionCaptor.getValue();
        Product savedProduct = productCaptor.getValue();

        assertEquals(originalQuantity + request.getQuantity(), existingProduct.getQuantity());
        assertEquals(originalQuantity + request.getQuantity(), savedProduct.getQuantity());
        assertEquals(existingProduct.getId(), savedProduct.getId());
        assertEquals(TransactionType.IN, savedTransaction.getType());
        assertEquals(request.getQuantity(), savedTransaction.getQuantity());
        assertEquals(request.getNote(), savedTransaction.getNote());
        assertEquals(existingProduct.getId(), savedTransaction.getProduct().getId());
    }

    @Test
    @DisplayName("stockIn should throw ResourceNotFoundException when product does not exist")
    void stockIn_withNonExistentProduct_shouldThrowResourceNotFoundException() {
        // Given
        StockRequest request = createStockRequest(99L);

        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                stockService.stockIn(request)
        );

        // Then
        assertEquals("Product not found", exception.getMessage());

        verify(productRepository, times(1)).findById(99L);
        verify(stockTransactionRepository, never()).save(any(StockTransaction.class));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("stockOut should decrease quantity and persist OUT transaction")
    void stockOut_withValidRequest_shouldDecreaseQuantityAndSaveTransaction() {
        // GIVEN
        Product existingProduct = createProduct(1L);
        StockRequest request = createStockRequest(1L);
        request.setNote("Stock out for customer order");
        int originalQuantity = existingProduct.getQuantity();

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(stockTransactionRepository.save(any(StockTransaction.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );
        when(productRepository.save(any(Product.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );

        //  WHEN
        stockService.stockOut(request);

        //  THEN
        ArgumentCaptor<StockTransaction> stockTransactionCaptor = ArgumentCaptor.forClass(StockTransaction.class);
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        verify(productRepository, times(1)).findById(1L);
        verify(stockTransactionRepository, times(1)).save(stockTransactionCaptor.capture());
        verify(productRepository, times(1)).save(productCaptor.capture());

        StockTransaction savedTransaction = stockTransactionCaptor.getValue();
        Product savedProduct = productCaptor.getValue();

        assertEquals(originalQuantity - request.getQuantity(), existingProduct.getQuantity());
        assertEquals(originalQuantity - request.getQuantity(), savedProduct.getQuantity());
        assertEquals(request.getNote(), savedTransaction.getNote());
        assertEquals(TransactionType.OUT, savedTransaction.getType());
        assertEquals(request.getQuantity(), savedTransaction.getQuantity());
        assertEquals(existingProduct.getId(), savedTransaction.getProduct().getId());
    }

    @Test
    @DisplayName("stockOut should throw RuntimeException when stock is insufficient")
    void stockOut_withInsufficientStock_shouldThrowRuntimeException() {
        // GIVEN
        Product existingProduct = createProduct(1L);
        StockRequest request = createStockRequest(1L);
        request.setQuantity(existingProduct.getQuantity() + 1); // Request more than available
        request.setNote("Stock out for customer order");
        int originalQuantity = existingProduct.getQuantity();

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));

        //  WHEN
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                stockService.stockOut(request)
        );

        //  THEN
        assertEquals("Insufficient stock", exception.getMessage());
        assertEquals(originalQuantity, existingProduct.getQuantity()); // Quantity should not change

        verify(productRepository, times(1)).findById(1L);
        verify(stockTransactionRepository, never()).save(any(StockTransaction.class));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("stockOut should throw ResourceNotFoundException when product does not exist")
    void stockOut_withNonExistentProduct_shouldThrowResourceNotFoundException() {
        // GIVEN
        StockRequest request = createStockRequest(99L);
        request.setNote("Stock out for customer order");

        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        //  WHEN
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                stockService.stockOut(request)
        );

        //  THEN
        assertEquals("Product not found", exception.getMessage());

        verify(productRepository, times(1)).findById(99L);
        verify(stockTransactionRepository, never()).save(any(StockTransaction.class));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("getHistory should return paginated stock transactions")
    void getHistory_shouldReturnPaginatedStockTransactions() {
        // GIVEN
        Pageable pageable = PageRequest.of(0, 10);
        Page<StockTransaction> stockTransactionPage = new PageImpl<>(
                List.of(
                        createStockTransaction(1L),
                        createStockTransaction(2L)
                )
        );

        when(stockTransactionRepository.findAll(pageable)).thenReturn(stockTransactionPage);

        // WHEN
        Page<StockTransactionResponse> result =  stockService.getHistory(pageable);

        // THEN
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(2L, result.getContent().get(1).getId());
        assertEquals("Product 1", result.getContent().get(0).getProductName());
        assertEquals("Product 2", result.getContent().get(1).getProductName());
        assertEquals(TransactionType.IN, result.getContent().get(0).getType());
        assertEquals(TransactionType.IN, result.getContent().get(1).getType());
        assertEquals(1, result.getContent().get(0).getQuantity());
        assertEquals(1, result.getContent().get(1).getQuantity());
        assertEquals("Restock from supplier", result.getContent().get(0).getNote());
        assertEquals("Restock from supplier", result.getContent().get(1).getNote());

        verify(stockTransactionRepository,  times(1)).findAll(pageable);
    }

    @Test
    void getHistoryByProductId_withExistingProductId_shouldReturnStockTransactions() {
        // GIVEN
        Product existingProduct = createProduct();
        Pageable pageable = PageRequest.of(0, 10);
        Page<StockTransaction> stockTransactionPage = new PageImpl<>(
                List.of(
                        createStockTransaction(1L),
                        createStockTransaction(2L)
                )
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(stockTransactionRepository.findByProductIdOrderByCreatedAtDesc(1L, pageable))
                .thenReturn(stockTransactionPage);

        // WHEN
        Page<StockHistoryResponse> result = stockService.getHistoryByProductId(1L, pageable);

        // THEN
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(2L, result.getContent().get(1).getId());
        assertEquals("IN", result.getContent().get(0).getType().name());
        assertEquals("IN", result.getContent().get(1).getType().name());
        assertEquals(1, result.getContent().get(0).getQuantity());
        assertEquals(1, result.getContent().get(1).getQuantity());
        assertEquals("Restock from supplier", result.getContent().get(0).getNote());
        assertEquals("Restock from supplier", result.getContent().get(1).getNote());

        verify(productRepository, times(1)).findById(1L);
        verify(stockTransactionRepository, times(1)).findByProductIdOrderByCreatedAtDesc(1L, pageable);
    }

    @Test
    void getHistoryByProductId_withNonExistentProductId_shouldThrowResourceNotFoundException() {
        // GIVEN
        Pageable pageable = PageRequest.of(0, 10);

        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                stockService.getHistoryByProductId(99L, pageable)
        );

        // THEN
        assertEquals("Product not found", exception.getMessage());

        verify(productRepository, times(1)).findById(99L);
        verify(stockTransactionRepository, never()).findByProductIdOrderByCreatedAtDesc(99L, pageable);
    }
}
