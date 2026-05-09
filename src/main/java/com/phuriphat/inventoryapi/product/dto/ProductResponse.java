package com.phuriphat.inventoryapi.product.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ProductResponse {
    private Long id;

    private String sku;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer quantity;

    private Integer lowStockThreshold;

    private String categoryName;

    private LocalDateTime createdAt;
}
