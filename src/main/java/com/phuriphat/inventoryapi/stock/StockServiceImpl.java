package com.phuriphat.inventoryapi.stock;

import com.phuriphat.inventoryapi.exception.ResourceNotFoundException;
import com.phuriphat.inventoryapi.product.Product;
import com.phuriphat.inventoryapi.product.ProductRepository;
import com.phuriphat.inventoryapi.stock.dto.StockHistoryResponse;
import com.phuriphat.inventoryapi.stock.dto.StockRequest;
import com.phuriphat.inventoryapi.stock.dto.StockTransactionResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final ProductRepository productRepository;
    private final StockTransactionRepository  stockTransactionRepository;

    @Override
    @Transactional
    public void stockIn(StockRequest stockRequest) {
        Product product = productRepository.findById(stockRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setQuantity(
                product.getQuantity() + stockRequest.getQuantity()
        );

        StockTransaction stockTransaction = StockTransaction.builder()
                .product(product)
                .type(TransactionType.IN)
                .quantity(stockRequest.getQuantity())
                .note(stockRequest.getNote())
                .createdAt(LocalDateTime.now())
                .build();

        stockTransactionRepository.save(stockTransaction);

        productRepository.save(product);
    }

    @Override
    @Transactional
    public void stockOut(StockRequest stockRequest) {
        Product product = productRepository.findById(stockRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getQuantity() < stockRequest.getQuantity()) {
            throw new RuntimeException("Insufficient stock");
        }

        product.setQuantity(product.getQuantity() - stockRequest.getQuantity());

        StockTransaction stockTransaction = StockTransaction.builder()
                .product(product)
                .type(TransactionType.OUT)
                .quantity(stockRequest.getQuantity())
                .note(stockRequest.getNote())
                .createdAt(LocalDateTime.now())
                .build();

        stockTransactionRepository.save(stockTransaction);

        productRepository.save(product);
    }

    @Override
    public List<StockTransactionResponse> getHistory() {
        return stockTransactionRepository.findAll()
                .stream()
                .map(stockTransaction -> StockTransactionResponse.builder()
                        .id(stockTransaction.getId())
                        .productName(stockTransaction.getProduct().getName())
                        .type(stockTransaction.getType())
                        .quantity(stockTransaction.getQuantity())
                        .note(stockTransaction.getNote())
                        .createdAt(stockTransaction.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    public List<StockHistoryResponse> getHistoryByProductId(Long productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return stockTransactionRepository.findByProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(stockTransaction -> StockHistoryResponse.builder()
                    .id(stockTransaction.getId())
                    .type(stockTransaction.getType())
                    .quantity(stockTransaction.getQuantity())
                    .note(stockTransaction.getNote())
                    .createdAt(stockTransaction.getCreatedAt())
                    .build()
                )
                .toList();
    }
}
