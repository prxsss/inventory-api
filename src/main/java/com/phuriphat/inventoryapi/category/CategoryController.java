package com.phuriphat.inventoryapi.category;

import com.phuriphat.inventoryapi.category.dto.CategoryResponse;
import com.phuriphat.inventoryapi.category.dto.CreateCategoryRequest;
import com.phuriphat.inventoryapi.common.ApiResponse;
import com.phuriphat.inventoryapi.common.PaginationHelper;
import com.phuriphat.inventoryapi.common.PaginationResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CreateCategoryRequest createCategoryRequest) {
        CategoryResponse response = categoryService.create(createCategoryRequest);
        ApiResponse<CategoryResponse> apiResponse = ApiResponse.<CategoryResponse>builder()
                .success(true)
                .message("Category created")
                .data(response)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<CategoryResponse>>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryResponse> response = categoryService.findAll(keyword, pageable);
        PaginationResponse<CategoryResponse> paginationResponse = PaginationHelper.toPaginationResponse(response);
        ApiResponse<PaginationResponse<CategoryResponse>> apiResponse = ApiResponse.<PaginationResponse<CategoryResponse>>builder()
                .success(true)
                .message("Categories fetched successfully")
                .data(paginationResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable Long id) {
        CategoryResponse response = categoryService.findById(id);
        ApiResponse<CategoryResponse> apiResponse = ApiResponse.<CategoryResponse>builder()
                .success(true)
                .message("Success")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(@PathVariable Long id, @Valid @RequestBody CreateCategoryRequest createCategoryRequest) {
        CategoryResponse response = categoryService.update(id,createCategoryRequest);
        ApiResponse<CategoryResponse> apiResponse = ApiResponse.<CategoryResponse>builder()
                .success(true)
                .message("Category updated")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        categoryService.delete(id);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .success(true)
                .message("Category deleted")
                .data(null)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
