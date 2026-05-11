package com.phuriphat.inventoryapi.stock;

import com.phuriphat.inventoryapi.stock.dto.StockHistoryResponse;
import com.phuriphat.inventoryapi.stock.dto.StockRequest;
import com.phuriphat.inventoryapi.stock.dto.StockTransactionResponse;

import java.util.List;

public interface StockService {

    void stockIn(StockRequest stockRequest);

    void stockOut(StockRequest stockRequest);

    List<StockTransactionResponse> getHistory();

    List<StockHistoryResponse> getHistoryByProductId(Long productId);
}
