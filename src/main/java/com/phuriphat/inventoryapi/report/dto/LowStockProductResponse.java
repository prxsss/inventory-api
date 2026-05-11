package com.phuriphat.inventoryapi.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LowStockProductResponse {

    private Long id;

    private String name;

    private Integer quantity;

    private Integer lowStockThreshold;
}
