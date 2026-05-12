package com.phuriphat.inventoryapi.stock.dto;

import com.phuriphat.inventoryapi.stock.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTransactionResponse {

    private Long id;

    private String productName;

    private TransactionType type;

    private Integer quantity;

    private String note;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
