package com.phuriphat.inventoryapi.report;

import com.phuriphat.inventoryapi.common.ApiResponse;
import com.phuriphat.inventoryapi.report.dto.InventorySummaryResponse;
import com.phuriphat.inventoryapi.report.dto.LowStockProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<LowStockProductResponse>>> getLowStockProducts() {
        List<LowStockProductResponse> response = reportService.getLowStockProducts();
        ApiResponse<List<LowStockProductResponse>> apiResponse = ApiResponse.<List<LowStockProductResponse>>builder()
                .success(true)
                .message("Success")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/summary")
      ResponseEntity<ApiResponse<InventorySummaryResponse>> getInventorySummary() {
          InventorySummaryResponse response = reportService.getInventorySummary();
          ApiResponse<InventorySummaryResponse> apiResponse = ApiResponse.<InventorySummaryResponse>builder()
                     .success(true)
                     .message("Success")
                     .data(response)
                     .build();

          return ResponseEntity.ok(apiResponse);
    }
}
