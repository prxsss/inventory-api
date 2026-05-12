package com.phuriphat.inventoryapi.report;

import com.phuriphat.inventoryapi.category.CategoryRepository;
import com.phuriphat.inventoryapi.product.ProductRepository;
import com.phuriphat.inventoryapi.report.dto.InventorySummaryResponse;
import com.phuriphat.inventoryapi.report.dto.LowStockProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ProductRepository productRepository;
    private final CategoryRepository  categoryRepository;

    @Override
    public Page<LowStockProductResponse> getLowStockProducts(Pageable pageable) {
        return productRepository.findLowStockProducts(pageable)
                .map(product -> LowStockProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .quantity(product.getQuantity())
                        .lowStockThreshold(product.getLowStockThreshold())
                        .build()
                );
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
