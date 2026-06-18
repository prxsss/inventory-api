package com.phuriphat.inventoryapi.stock;

import com.phuriphat.inventoryapi.stock.dto.StockHistoryResponse;
import com.phuriphat.inventoryapi.stock.dto.StockRequest;
import com.phuriphat.inventoryapi.stock.dto.StockTransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StockService {

    void stockIn(StockRequest stockRequest, String createdBy);

    void stockOut(StockRequest stockRequest, String createdBy);

    Page<StockTransactionResponse> getHistory(Pageable pageable);

    Page<StockHistoryResponse> getHistoryByProductId(Long productId, Pageable pageable);
}
