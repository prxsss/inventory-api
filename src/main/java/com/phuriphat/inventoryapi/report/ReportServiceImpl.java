package com.phuriphat.inventoryapi.report;

import com.phuriphat.inventoryapi.category.CategoryRepository;
import com.phuriphat.inventoryapi.product.ProductRepository;
import com.phuriphat.inventoryapi.report.dto.InventorySummaryResponse;
import com.phuriphat.inventoryapi.report.dto.LowStockProductResponse;
import com.phuriphat.inventoryapi.stock.StockTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ProductRepository productRepository;
    private final CategoryRepository  categoryRepository;
    private final StockTransactionRepository stockTransactionRepository;

    @Override
    public List<LowStockProductResponse> getLowStockProducts() {
        return productRepository.findLowStockProducts()
                .stream()
                .map(product -> LowStockProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .quantity(product.getQuantity())
                        .lowStockThreshold(product.getLowStockThreshold())
                        .build()
                )
                .toList();
    }

    @Override
    public InventorySummaryResponse getInventorySummary() {
        long totalProducts = productRepository.count();
        long totalCategories = categoryRepository.count();
        long lowStockProducts = productRepository.countLowStockProducts();
        int totalQuantity = productRepository.getTotalQuantity();

        return InventorySummaryResponse.builder()
                .totalProducts(totalProducts)
                .totalCategories(totalCategories)
                .lowStockProducts(lowStockProducts)
                .totalQuantity(totalQuantity)
                .build();
    }
}
