package com.phuriphat.inventoryapi.report;

import com.phuriphat.inventoryapi.report.dto.InventorySummaryResponse;
import com.phuriphat.inventoryapi.report.dto.LowStockProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportService {

    Page<LowStockProductResponse> getLowStockProducts(Pageable pageable);

    InventorySummaryResponse getInventorySummary();
}
