package com.kumar.productservice.service;

import com.kumar.productservice.dto.CategoryRequestDTO;
import com.kumar.productservice.dto.CategoryResponseDTO;
import com.kumar.productservice.dto.UpdateCategoryDTO;
import org.springframework.data.domain.Page;

public interface CategoryService {

    // Create Category
    CategoryResponseDTO createCategory(
            CategoryRequestDTO requestDTO
    );

    // Get Category By Id
    CategoryResponseDTO getCategoryById(
            Long categoryId
    );

    // Get All Categories (Pagination)
    Page<CategoryResponseDTO> getAllCategories(
            int page,
            int size,
            String sortBy,
            String direction
    );

    // Update Category
    CategoryResponseDTO updateCategory(
            Long categoryId,
            UpdateCategoryDTO updateDTO
    );

    // Delete Category
    void deleteCategory(
            Long categoryId
    );

}