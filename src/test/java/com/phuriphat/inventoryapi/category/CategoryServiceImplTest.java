package com.phuriphat.inventoryapi.category;

import com.phuriphat.inventoryapi.category.dto.CategoryOptionProjection;
import com.phuriphat.inventoryapi.category.dto.CategoryResponse;
import com.phuriphat.inventoryapi.category.dto.CreateCategoryRequest;
import com.phuriphat.inventoryapi.exception.DuplicateResourceException;
import com.phuriphat.inventoryapi.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private final String name = "Electronics";

    private CreateCategoryRequest createCategoryRequest() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName(name);
        return request;
    }

    private Category createCategory(Long id) {
        return Category.builder()
                .id(id)
                .name(name)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("create should return saved category details when name is unique")
    void create_withValidCategoryName_shouldReturnSavedCategoryDetails() {
        // [1] GIVEN
        CreateCategoryRequest request = createCategoryRequest();

        when(categoryRepository.existsByNameIgnoreCase(name)).thenReturn(false);

        Category savedCategory = createCategory(1L);

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        // [2] WHEN
        CategoryResponse response = categoryService.create(request);

        // [3] THEN
        assertNotNull(response, "Response should not be null");
        assertEquals(1L, response.getId(), "Id should match");
        assertEquals(name, response.getName(), "Name should match");

        verify(categoryRepository, times(1)).existsByNameIgnoreCase(name);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("create should throw DuplicateResourceException when name already exists")
    void create_withDuplicateCategoryName_shouldThrowDuplicateResourceException() {
        // [1] GIVEN
        CreateCategoryRequest request = createCategoryRequest();

        // จำลองสถานการณ์: เช็กชื่อซ้ำแล้วได้ true (ซ้ำ!)
        when(categoryRepository.existsByNameIgnoreCase(name)).thenReturn(true);

        // [2] WHEN & [3] THEN
        // ในกรณีที่คาดหวังว่าจะเกิด Exception เราจะใช้ assertThrows ในการดักจับ
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () ->
            categoryService.create(request)
        );

        // ตรวจสอบว่า Message ใน Exception ตรงกับที่เราเขียนดักไว้ไหม
        assertEquals("Category with name Electronics already exists", exception.getMessage());

        // ตรวจสอบให้มั่นใจว่า โค้ดต้องหยุดทำงานทันที และ "ต้องไม่มีการวิ่งไปเรียกคำสั่ง save เด็ดขาด"
        verify(categoryRepository, times(1)).existsByNameIgnoreCase(name);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("getAll should return matching categories page when keyword is provided")
    void getAll_withKeyword_shouldReturnAllMatchingCategoriesPage() {
        // [1] GIVEN
        String keyword = "electronics";
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(createCategory(1L), createCategory(2L));
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findByNameContainingIgnoreCase(keyword, pageable)).thenReturn(categoryPage);

        // [2] WHEN
        Page<CategoryResponse> result = categoryService.findAll(keyword, pageable);

        // [3] THEN
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.getTotalElements(), "Total elements should match");
        assertEquals(1L, result.getContent().get(0).getId(), "First category ID should match");
        assertEquals(name, result.getContent().get(0).getName(), "First category name should match");
        assertEquals(2L, result.getContent().get(1).getId(), "Second category ID should match");
        assertEquals(name, result.getContent().get(1).getName(), "Second category name should match");

        verify(categoryRepository, times(1)).findByNameContainingIgnoreCase(keyword, pageable);
    }

    @Test
    @DisplayName("getAll should search with empty string when keyword is null")
    void getAll_withNullKeyword_shouldSearchWithEmptyString() {
        // [1] GIVEN
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(createCategory(1L), createCategory(2L), createCategory(3L));
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findByNameContainingIgnoreCase("", pageable)).thenReturn(categoryPage);

        // [2] WHEN
        Page<CategoryResponse> result = categoryService.findAll(null, pageable);

        // [3] THEN
        assertNotNull(result, "Result should not be null");
        assertEquals(3, result.getTotalElements(), "Total elements should match");

        verify(categoryRepository, times(1)).findByNameContainingIgnoreCase("", pageable);
    }

    @Test
    @DisplayName("getAll should search with empty string when keyword is blank")
    void getAll_withBlankKeyword_shouldSearchWithEmptyString() {
        // [1] GIVEN
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(createCategory(1L), createCategory(2L), createCategory(3L));
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findByNameContainingIgnoreCase("", pageable)).thenReturn(categoryPage);

        // [2] WHEN
        Page<CategoryResponse> result = categoryService.findAll("    ", pageable);

        // [3] THEN
        assertNotNull(result, "Result should not be null");
        assertEquals(3, result.getTotalElements(), "Total elements should match");

        verify(categoryRepository, times(1)).findByNameContainingIgnoreCase("", pageable);
    }

    @Test
    @DisplayName("getCategoryById should return category response when ID exists")
    void getCategoryById_withExistingId_shouldReturnCategoryResponse() {
        // [1] GIVEN
        Category category = createCategory(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // [2] WHEN
        CategoryResponse result = categoryService.findById(1L);

        // [3] THEN
        assertNotNull(result, "Result should not be null");
        assertEquals(1L, result.getId(), "Category ID should match");
        assertEquals(name, result.getName(), "Category should match");

        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getCategoryById should throw ResourceNotFoundException when ID does not exist")
    void getCategoryById_withNonExistentId_shouldThrowResourceNotFoundException() {
        // [1] GIVEN
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        // [2] WHEN
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                categoryService.findById(99L)
        );

        // [3] THEN
        assertEquals("Category not found", exception.getMessage(), "Exception message should match");

        verify(categoryRepository, times(1)).findById(99L);
    }

    @Test
    void getAllForOption_shouldReturnListOfCategoryOptionProjection() {
        // GIVEN
        List<CategoryOptionProjection> categoryOptions = List.of(
                CategoryOptionProjection.builder()
                        .id(1L)
                        .name("First Category")
                        .build(),
                CategoryOptionProjection.builder()
                        .id(2L)
                        .name("Second Category")
                        .build()
        );

        when(categoryRepository.findAllProjectedBy()).thenReturn(categoryOptions);

        // WHEN
        List<CategoryOptionProjection> result = categoryService.findAllForOption();

        // THEN
        assertNotNull(result, "Result should not be null");
        assertEquals(categoryOptions.size(), result.size(), "Size of result should match");
        assertEquals(1L, result.get(0).getId(), "First category ID should match");
        assertEquals("First Category", result.get(0).getName(), "First category name should match");
        assertEquals(2L, result.get(1).getId(), "Second category ID should match");
        assertEquals("Second Category", result.get(1).getName());

        verify(categoryRepository, times(1)).findAllProjectedBy();
    }

    @Test
    @DisplayName("updateCategory should return updated category response when request is valid")
    void updateCategory_withValidRequest_shouldReturnUpdatedCategoryResponse() {
        // [1] GIVEN
        Category existingCategory = createCategory(1L);

        CreateCategoryRequest request = createCategoryRequest();
        request.setName("Updated Name");

        Category updatedCategory = createCategory(1L);
        updatedCategory.setName("Updated Name");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        // [2] WHEN
        CategoryResponse result = categoryService.update(1L, request);

        // [3] THEN
        assertNotNull(result, "Result should not be null");
        assertEquals(1L, result.getId(), "Category ID should match");
        assertEquals("Updated Name", result.getName(), "Category name should be updated");

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("updateCategory should throw ResourceNotFoundException when category does not exist")
    void updateCategory_withNonExistentId_shouldThrowResourceNotFoundException() {
        // [1] GIVEN
        CreateCategoryRequest request = createCategoryRequest();

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        // [2] WHEN
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                categoryService.update(99L, request)
        );

        // [3] THEN
        assertEquals("Category not found", exception.getMessage(), "Exception message should match");

        verify(categoryRepository, times(1)).findById(99L);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("deleteCategory should remove category when ID exists")
    void deleteCategory_withExistingId_shouldDeleteSuccessfully() {
        // [1] GIVEN
        Category category = createCategory(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).deleteById(1L);

        // [2] WHEN
        categoryService.delete(1L);

        // [3] THEN
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteCategory should throw ResourceNotFoundException when category does not exist")
    void deleteCategory_withNonExistentId_shouldThrowResourceNotFoundException() {
        // [1] GIVEN
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        // [2] WHEN
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                categoryService.delete(99L)
        );

        // [3] THEN
        assertEquals("Category not found", exception.getMessage(), "Exception message should match");

        verify(categoryRepository, times(1)).findById(99L);
        verify(categoryRepository, never()).deleteById(any());
    }
}
