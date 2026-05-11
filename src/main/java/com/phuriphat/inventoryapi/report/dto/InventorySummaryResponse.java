package com.phuriphat.inventoryapi.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventorySummaryResponse {

    private long totalProducts;

    private long totalCategories;

    private long lowStockProducts;

    private int totalQuantity;
}
