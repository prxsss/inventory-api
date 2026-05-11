package com.phuriphat.inventoryapi.report;

import com.phuriphat.inventoryapi.report.dto.InventorySummaryResponse;
import com.phuriphat.inventoryapi.report.dto.LowStockProductResponse;

import java.util.List;

public interface ReportService {

    List<LowStockProductResponse> getLowStockProducts();

    InventorySummaryResponse getInventorySummary();
}
