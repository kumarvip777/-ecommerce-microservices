package com.kumar.productservice.repo;

import com.kumar.productservice.entity.Category;
import com.kumar.productservice.entity.enums.CategoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByCategoryName(String categoryName);

    Optional<Category> findByCategoryName(String categoryName);

    List<Category> findByStatus(CategoryStatus status);

    List<Category> findByCategoryNameContainingIgnoreCase(
            String categoryName
    );

}