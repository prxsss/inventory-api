package com.phuriphat.inventoryapi.stock.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockRequest {

    @NotNull
    private Long productId;

    @Positive
    private Integer quantity;

    private String note;
}
