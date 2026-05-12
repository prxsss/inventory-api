package com.phuriphat.inventoryapi.stock;

import com.phuriphat.inventoryapi.common.ApiResponse;
import com.phuriphat.inventoryapi.common.PaginationHelper;
import com.phuriphat.inventoryapi.common.PaginationResponse;
import com.phuriphat.inventoryapi.stock.dto.StockHistoryResponse;
import com.phuriphat.inventoryapi.stock.dto.StockRequest;
import com.phuriphat.inventoryapi.stock.dto.StockTransactionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ApiResponse<PaginationResponse<StockTransactionResponse>>> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StockTransactionResponse> response = stockService.getHistory(pageable);
        PaginationResponse<StockTransactionResponse> paginationResponse = PaginationHelper.toPaginationResponse(response);
        ApiResponse<PaginationResponse<StockTransactionResponse>> apiResponse = ApiResponse.<PaginationResponse<StockTransactionResponse>>builder()
                .success(true)
                .message("Stock history fetched successfully")
                .data(paginationResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }


    @GetMapping("/history/{productId}")
    public ResponseEntity<ApiResponse<PaginationResponse<StockHistoryResponse>>> getHistoryByProductId(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StockHistoryResponse> response = stockService.getHistoryByProductId(productId, pageable);
        PaginationResponse<StockHistoryResponse> paginationResponse = PaginationHelper.toPaginationResponse(response);
        ApiResponse<PaginationResponse<StockHistoryResponse>> apiResponse = ApiResponse.<PaginationResponse<StockHistoryResponse>>builder()
                .success(true)
                .message("Stock history fetched successfully")
                .data(paginationResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
