package com.kumar.productservice.dto;

import com.kumar.productservice.entity.enums.ProductStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductResponseDTO {

    private Long productId;

    private String productName;

    private String description;

    private String brand;

    private BigDecimal price;

    private Integer stockQuantity;

    private ProductStatus status;

    private Long categoryId;

    private String categoryName;

    private String imageUrl;

}