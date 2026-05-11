package com.phuriphat.inventoryapi.stock;

import com.phuriphat.inventoryapi.common.ApiResponse;
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
    public ResponseEntity<ApiResponse<Void>> stockIn(
            @Valid @RequestBody StockRequest stockRequest
    ) {
        stockService.stockIn(stockRequest);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .success(true)
                .message("Stock added")
                .data(null)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/out")
    public ResponseEntity<ApiResponse<Void>> stockOut(
            @Valid @RequestBody StockRequest stockRequest
    ) {
        stockService.stockOut(stockRequest);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .success(true)
                .message("Stock removed")
                .data(null)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<StockTransactionResponse>>> getHistory() {
        List<StockTransactionResponse> response = stockService.getHistory();
        ApiResponse<List<StockTransactionResponse>> apiResponse = ApiResponse.<List<StockTransactionResponse>>builder()
                .success(true)
                .message("Success")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }


    @GetMapping("/history/{productId}")
    public ResponseEntity<ApiResponse<List<StockHistoryResponse>>> getHistoryByProductId(
            @PathVariable Long productId
    ) {
        List<StockHistoryResponse> response = stockService.getHistoryByProductId(productId);
        ApiResponse<List<StockHistoryResponse>> apiResponse = ApiResponse.<List<StockHistoryResponse>>builder()
                .success(true)
                .message("Success")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
