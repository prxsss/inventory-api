package com.phuriphat.inventoryapi.stock;

import com.phuriphat.inventoryapi.stock.dto.StockHistoryResponse;
import com.phuriphat.inventoryapi.stock.dto.StockRequest;
import com.phuriphat.inventoryapi.stock.dto.StockTransactionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping("/in")
    public ResponseEntity<String> stockIn(
            @Valid @RequestBody StockRequest stockRequest
    ) {
        stockService.stockIn(stockRequest);

        return ResponseEntity.ok("Stock added");
    }

    @PostMapping("/out")
    public ResponseEntity<String> stockOut(
            @Valid @RequestBody StockRequest stockRequest
    ) {
        stockService.stockOut(stockRequest);

        return ResponseEntity.ok("Stock removed");
    }

    @GetMapping("/history")
    public ResponseEntity<List<StockTransactionResponse>> getHistory() {
        return ResponseEntity.ok(stockService.getHistory());
    }


    @GetMapping("/history/{productId}")
    public ResponseEntity<List<StockHistoryResponse>> getHistoryByProductId(
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(stockService.getHistoryByProductId(productId));
    }
}
