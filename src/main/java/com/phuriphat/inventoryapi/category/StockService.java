package com.phuriphat.inventoryapi.category;

import com.phuriphat.inventoryapi.stock.dto.StockRequest;
import com.phuriphat.inventoryapi.stock.dto.StockTransactionResponse;

import java.util.List;

public interface StockService {

    void stockIn(StockRequest stockRequest);

    void stockOut(StockRequest stockRequest);

    List<StockTransactionResponse> getHistory();
}
