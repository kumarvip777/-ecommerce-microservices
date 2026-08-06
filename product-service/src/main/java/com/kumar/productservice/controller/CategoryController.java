package com.kumar.productservice.controller;

import com.kumar.productservice.dto.CategoryRequestDTO;
import com.kumar.productservice.dto.CategoryResponseDTO;
import com.kumar.productservice.dto.UpdateCategoryDTO;
import com.kumar.productservice.payload.ApiResponse;
import com.kumar.productservice.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(
        name = "Category Management",
        description = "APIs for managing product categories"
)
public class CategoryController {

    private static final Logger logger =
            LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    @Operation(
            summary = "Create Category",
            description = "Creates a new category"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> createCategory(

            @Valid
            @RequestBody CategoryRequestDTO requestDTO,

            HttpServletRequest request) {

        logger.info("Received request to create category");

        CategoryResponseDTO responseDTO =
                categoryService.createCategory(requestDTO);

        ApiResponse<CategoryResponseDTO> response =
                ApiResponse.success(
                        responseDTO,
                        "Category created successfully",
                        request.getRequestURI()
                );

        logger.info(
                "Category created successfully. categoryId={}",
                responseDTO.getCategoryId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get Category By Id",
            description = "Returns category details using category id"
    )
    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> getCategoryById(

            @PathVariable Long categoryId,

            HttpServletRequest request) {

        logger.info(
                "Received request to fetch categoryId: {}",
                categoryId
        );

        CategoryResponseDTO responseDTO =
                categoryService.getCategoryById(categoryId);

        ApiResponse<CategoryResponseDTO> response =
                ApiResponse.success(
                        responseDTO,
                        "Category fetched successfully",
                        request.getRequestURI()
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get All Categories",
            description = "Returns paginated list of categories"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CategoryResponseDTO>>> getAllCategories(

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size,

            @RequestParam(defaultValue = "categoryId") String sortBy,

            @RequestParam(defaultValue = "asc") String direction,

            HttpServletRequest request) {

        logger.info("Received request to fetch all categories");

        Page<CategoryResponseDTO> responseDTO =
                categoryService.getAllCategories(
                        page,
                        size,
                        sortBy,
                        direction
                );

        ApiResponse<Page<CategoryResponseDTO>> response =
                ApiResponse.success(
                        responseDTO,
                        "Categories fetched successfully",
                        request.getRequestURI()
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update Category",
            description = "Updates category details"
    )
    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> updateCategory(

            @PathVariable Long categoryId,

            @Valid
            @RequestBody UpdateCategoryDTO updateDTO,

            HttpServletRequest request) {

        logger.info(
                "Received request to update categoryId: {}",
                categoryId
        );

        CategoryResponseDTO responseDTO =
                categoryService.updateCategory(
                        categoryId,
                        updateDTO
                );

        ApiResponse<CategoryResponseDTO> response =
                ApiResponse.success(
                        responseDTO,
                        "Category updated successfully",
                        request.getRequestURI()
                );

        logger.info(
                "Category updated successfully. categoryId={}",
                categoryId
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete Category",
            description = "Deletes category by category id"
    )
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(

            @PathVariable Long categoryId,

            HttpServletRequest request) {

        logger.info(
                "Received request to delete categoryId: {}",
                categoryId
        );

        categoryService.deleteCategory(categoryId);

        ApiResponse<String> response =
                ApiResponse.success(
                        "Category deleted successfully",
                        "Category deleted successfully",
                        request.getRequestURI()
                );

        logger.info(
                "Category deleted successfully. categoryId={}",
                categoryId
        );

        return ResponseEntity.ok(response);
    }
}