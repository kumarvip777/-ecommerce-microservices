package com.kumar.productservice.repo;

import com.kumar.productservice.entity.Product;
import com.kumar.productservice.entity.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByProductName(String productName);

    Optional<Product> findByProductName(String productName);

    List<Product> findByStatus(ProductStatus status);

    List<Product> findByCategoryCategoryId(Long categoryId);

    List<Product> findByProductNameContainingIgnoreCase(
            String productName
    );

    List<Product> findByBrandContainingIgnoreCase(
            String brand
    );

    List<Product> findByPriceBetween(
            BigDecimal minPrice,
            BigDecimal maxPrice
    );
}