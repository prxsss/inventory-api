package com.phuriphat.inventoryapi.report;

import com.phuriphat.inventoryapi.common.ApiResponse;
import com.phuriphat.inventoryapi.common.PaginationHelper;
import com.phuriphat.inventoryapi.common.PaginationResponse;
import com.phuriphat.inventoryapi.report.dto.InventorySummaryResponse;
import com.phuriphat.inventoryapi.report.dto.LowStockProductResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<PaginationResponse<LowStockProductResponse>>> getLowStockProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<LowStockProductResponse> response = reportService.getLowStockProducts(pageable);
        PaginationResponse<LowStockProductResponse> paginationResponse = PaginationHelper.toPaginationResponse(response);
        ApiResponse<PaginationResponse<LowStockProductResponse>> apiResponse = ApiResponse.<PaginationResponse<LowStockProductResponse>>builder()
                .success(true)
                .message("Low stock products fetched successfully")
                .data(paginationResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<InventorySummaryResponse>> getInventorySummary() {
        InventorySummaryResponse response = reportService.getInventorySummary();
        ApiResponse<InventorySummaryResponse> apiResponse = ApiResponse.<InventorySummaryResponse>builder()
                    .success(true)
                    .message("Success")
                    .data(response)
                    .build();

        return ResponseEntity.ok(apiResponse);
    }
}
