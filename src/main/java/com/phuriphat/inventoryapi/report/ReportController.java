package com.phuriphat.inventoryapi.report;

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
    public ResponseEntity<List<LowStockProductResponse>> getLowStockProducts() {
        return ResponseEntity.ok(reportService.getLowStockProducts());
    }

    @GetMapping("/summary")
     ResponseEntity<InventorySummaryResponse> getInventorySummary() {
        return ResponseEntity.ok(reportService.getInventorySummary());
    }
}
