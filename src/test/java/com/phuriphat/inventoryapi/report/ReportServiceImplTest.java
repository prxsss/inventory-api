package com.phuriphat.inventoryapi.report;

import com.phuriphat.inventoryapi.category.CategoryRepository;
import com.phuriphat.inventoryapi.product.Product;
import com.phuriphat.inventoryapi.product.ProductRepository;
import com.phuriphat.inventoryapi.report.dto.InventorySummaryResponse;
import com.phuriphat.inventoryapi.report.dto.LowStockProductResponse;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    @Test
    @DisplayName("getLowStockProducts should return mapped low stock products page")
    void getLowStockProducts_shouldReturnMappedLowStockProductsPage() {
        // GIVEN
        Pageable pageable = PageRequest.of(0, 10);
        Product product = Product.builder()
                .id(1L)
                .name("Keyboard")
                .quantity(3)
                .lowStockThreshold(5)
                .build();
        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);

        when(productRepository.findLowStockProducts(pageable)).thenReturn(productPage);

        // WHEN
        Page<LowStockProductResponse> result = reportService.getLowStockProducts(pageable);

        // THEN
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().getFirst().getId());
        assertEquals("Keyboard", result.getContent().getFirst().getName());
        assertEquals(3, result.getContent().getFirst().getQuantity());
        assertEquals(5, result.getContent().getFirst().getLowStockThreshold());

        verify(productRepository, times(1)).findLowStockProducts(pageable);
    }

    @Test
    @DisplayName("getInventorySummary should return inventory summary response")
    void getInventorySummary_shouldReturnInventorySummaryResponse() {
        // GIVEN
        when(productRepository.count()).thenReturn(12L);
        when(categoryRepository.count()).thenReturn(4L);
        when(productRepository.countLowStockProducts()).thenReturn(2L);
        when(productRepository.getTotalQuantity()).thenReturn(87);

        // WHEN
        InventorySummaryResponse result = reportService.getInventorySummary();

        // THEN
        assertNotNull(result);
        assertEquals(12L, result.getTotalProducts());
        assertEquals(4L, result.getTotalCategories());
        assertEquals(2L, result.getLowStockProducts());
        assertEquals(87, result.getTotalQuantity());

        verify(productRepository, times(1)).count();
        verify(categoryRepository, times(1)).count();
        verify(productRepository, times(1)).countLowStockProducts();
        verify(productRepository, times(1)).getTotalQuantity();
    }
}
