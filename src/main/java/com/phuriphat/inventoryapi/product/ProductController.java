package com.phuriphat.inventoryapi.product;

import com.phuriphat.inventoryapi.common.ApiResponse;
import com.phuriphat.inventoryapi.common.PaginationHelper;
import com.phuriphat.inventoryapi.common.PaginationResponse;
import com.phuriphat.inventoryapi.product.dto.CreateProductRequest;
import com.phuriphat.inventoryapi.product.dto.ProductOptionProjection;
import com.phuriphat.inventoryapi.product.dto.ProductResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody CreateProductRequest createProductRequest) {
        ProductResponse response =  productService.create(createProductRequest);
        ApiResponse<ProductResponse> apiResponse = ApiResponse.<ProductResponse>builder()
                .success(true)
                .message("Product created")
                .data(response)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<ProductResponse>>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<ProductResponse> responses = productService.getAll(keyword, pageable);
        PaginationResponse<ProductResponse> paginationResponse = PaginationHelper.toPaginationResponse(responses);
        ApiResponse<PaginationResponse<ProductResponse>> apiResponse = ApiResponse.<PaginationResponse<ProductResponse>>builder()
                .success(true)
                .message("Products fetched successfully")
                .data(paginationResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable Long id) {
        ProductResponse response =  productService.getById(id);
        ApiResponse<ProductResponse> apiResponse = ApiResponse.<ProductResponse>builder()
                .success(true)
                .message("Success")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/options")
    public ResponseEntity<ApiResponse<List<ProductOptionProjection>>> getOptions() {
        List<ProductOptionProjection> response = productService.findAllForOption();
        ApiResponse<List<ProductOptionProjection>> apiResponse = ApiResponse.<List<ProductOptionProjection>>builder()
                .success(true)
                .message("Success")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable Long id, @Valid @RequestBody CreateProductRequest createProductRequest) {
        ProductResponse response =  productService.update(id, createProductRequest);
        ApiResponse<ProductResponse> apiResponse = ApiResponse.<ProductResponse>builder()
                .success(true)
                .message("Product updated")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.delete(id);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .success(true)
                .message("Product deleted")
                .data(null)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
