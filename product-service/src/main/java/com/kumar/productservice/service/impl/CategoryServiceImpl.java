package com.kumar.productservice.service.impl;

import com.kumar.productservice.dto.CategoryRequestDTO;
import com.kumar.productservice.dto.CategoryResponseDTO;
import com.kumar.productservice.dto.UpdateCategoryDTO;
import com.kumar.productservice.entity.Category;
import com.kumar.productservice.entity.enums.CategoryStatus;
import com.kumar.productservice.exception.category.CategoryAlreadyExistsException;
import com.kumar.productservice.exception.category.CategoryNotFoundException;
import com.kumar.productservice.exception.category.NoCategoriesFoundException;
import com.kumar.productservice.repo.CategoryRepository;
import com.kumar.productservice.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger =
            LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public CategoryResponseDTO createCategory(
            @Valid CategoryRequestDTO requestDTO) {

        logger.info(
                "Starting category creation for categoryName: {}",
                requestDTO.getCategoryName()
        );

        String categoryName =
                requestDTO.getCategoryName().trim();

        if (categoryRepository.existsByCategoryName(categoryName)) {

            throw new CategoryAlreadyExistsException(
                    "Category already exists: " + categoryName
            );
        }

        Category category =
                modelMapper.map(
                        requestDTO,
                        Category.class
                );

        category.setCategoryName(categoryName);

        category.setStatus(CategoryStatus.ACTIVE);

        Category savedCategory =
                categoryRepository.save(category);

        logger.info(
                "Category created successfully. categoryId={}, categoryName={}",
                savedCategory.getCategoryId(),
                savedCategory.getCategoryName()
        );

        return modelMapper.map(
                savedCategory,
                CategoryResponseDTO.class
        );
    }

    @Override
    public CategoryResponseDTO getCategoryById(
            Long categoryId) {

        logger.info(
                "Fetching category with id: {}",
                categoryId
        );

        Category category =
                categoryRepository.findById(categoryId)
                        .orElseThrow(() ->
                                new CategoryNotFoundException(
                                        "Category not found with id: "
                                                + categoryId
                                )
                        );

        logger.info(
                "Category fetched successfully. categoryId={}",
                categoryId
        );

        return modelMapper.map(
                category,
                CategoryResponseDTO.class
        );
    }

    @Override
    public Page<CategoryResponseDTO> getAllCategories(
            int page,
            int size,
            String sortBy,
            String direction) {

        logger.info("Fetching all categories");

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sort
                );

        Page<Category> categoryPage =
                categoryRepository.findAll(pageable);

        if (categoryPage.isEmpty()) {

            throw new NoCategoriesFoundException(
                    "No categories found"
            );
        }

        logger.info(
                "Fetched {} categories",
                categoryPage.getTotalElements()
        );

        return categoryPage.map(category ->
                modelMapper.map(
                        category,
                        CategoryResponseDTO.class
                )
        );
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(
            Long categoryId,
            @Valid UpdateCategoryDTO updateDTO) {

        logger.info(
                "Updating category with id: {}",
                categoryId
        );

        if (updateDTO.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one field must be provided for update"
            );
        }

        Category category =
                categoryRepository.findById(categoryId)
                        .orElseThrow(() ->
                                new CategoryNotFoundException(
                                        "Category not found with id: "
                                                + categoryId
                                )
                        );

        applyCategoryNameUpdate(category, updateDTO);

        applyDescriptionUpdate(category, updateDTO);

        Category updatedCategory =
                categoryRepository.save(category);

        logger.info(
                "Category updated successfully. categoryId={}",
                updatedCategory.getCategoryId()
        );

        return modelMapper.map(
                updatedCategory,
                CategoryResponseDTO.class
        );
    }

    @Override
    @Transactional
    public void deleteCategory(
            Long categoryId) {

        logger.info(
                "Deleting category with id: {}",
                categoryId
        );

        Category category =
                categoryRepository.findById(categoryId)
                        .orElseThrow(() ->
                                new CategoryNotFoundException(
                                        "Category not found with id: "
                                                + categoryId
                                )
                        );

        categoryRepository.delete(category);

        logger.info(
                "Category deleted successfully. categoryId={}",
                categoryId
        );
    }
    private void applyCategoryNameUpdate(
            Category category,
            UpdateCategoryDTO updateDTO) {

        if (updateDTO.getCategoryName() == null
                || updateDTO.getCategoryName().isBlank()) {
            return;
        }

        String categoryName =
                updateDTO.getCategoryName().trim();

        if (categoryRepository.existsByCategoryName(categoryName)
                && !category.getCategoryName().equalsIgnoreCase(categoryName)) {

            throw new CategoryAlreadyExistsException(
                    "Category already exists: " + categoryName
            );
        }

        category.setCategoryName(categoryName);

        logger.info(
                "Category name updated successfully"
        );
    }
    private void applyDescriptionUpdate(
            Category category,
            UpdateCategoryDTO updateDTO) {

        if (updateDTO.getDescription() == null
                || updateDTO.getDescription().isBlank()) {
            return;
        }

        category.setDescription(
                updateDTO.getDescription().trim()
        );

        logger.info(
                "Category description updated successfully"
        );
    }


}