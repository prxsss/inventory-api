package com.phuriphat.inventoryapi.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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

    private LocalDateTime createdAt;
}
