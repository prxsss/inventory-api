package com.phuriphat.inventoryapi.stock;

import com.phuriphat.inventoryapi.exception.ResourceNotFoundException;
import com.phuriphat.inventoryapi.product.Product;
import com.phuriphat.inventoryapi.product.ProductRepository;
import com.phuriphat.inventoryapi.stock.dto.StockHistoryResponse;
import com.phuriphat.inventoryapi.stock.dto.StockRequest;
import com.phuriphat.inventoryapi.stock.dto.StockTransactionResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final ProductRepository productRepository;
    private final StockTransactionRepository stockTransactionRepository;

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
            throw new InsufficientStockException("Insufficient stock");
        }

        product.setQuantity(product.getQuantity() - stockRequest.getQuantity());

        StockTransaction stockTransaction = StockTransaction.builder()
                .product(product)
                .type(TransactionType.OUT)
                .quantity(stockRequest.getQuantity())
                .note(stockRequest.getNote())
                .build();

        stockTransactionRepository.save(stockTransaction);

        productRepository.save(product);
    }

    @Override
        public Page<StockTransactionResponse> getHistory(Pageable pageable) {
        return stockTransactionRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(stockTransaction -> StockTransactionResponse.builder()
                        .id(stockTransaction.getId())
                        .productName(stockTransaction.getProduct().getName())
                        .type(stockTransaction.getType())
                        .quantity(stockTransaction.getQuantity())
                        .note(stockTransaction.getNote())
                        .createdAt(stockTransaction.getCreatedAt())
                        .updatedAt(stockTransaction.getUpdatedAt())
                        .build());
    }

    @Override
    public Page<StockHistoryResponse> getHistoryByProductId(Long productId, Pageable pageable) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return stockTransactionRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable)
                .map(stockTransaction -> StockHistoryResponse.builder()
                        .id(stockTransaction.getId())
                        .type(stockTransaction.getType())
                        .quantity(stockTransaction.getQuantity())
                        .note(stockTransaction.getNote())
                        .createdAt(stockTransaction.getCreatedAt())
                        .updatedAt(stockTransaction.getUpdatedAt())
                        .build()
                );
    }
}
