package com.kumar.productservice.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequestDTO {

    @NotBlank(message = "Product name is required")
    @Size(
            min = 2,
            max = 150,
            message = "Product name must be between 2 and 150 characters"
    )
    private String productName;

    @Size(
            max = 1000,
            message = "Description cannot exceed 1000 characters"
    )
    private String description;

    @NotBlank(message = "Brand is required")
    @Size(
            max = 100,
            message = "Brand cannot exceed 100 characters"
    )
    private String brand;

    @NotNull(message = "Price is required")
    @DecimalMin(
            value = "0.01",
            message = "Price must be greater than zero"
    )
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(
            value = 0,
            message = "Stock quantity cannot be negative"
    )
    private Integer stockQuantity;

    @NotNull(message = "Category id is required")
    private Long categoryId;

    @NotBlank(message = "Image URL is required")
    @Size(
            max = 500,
            message = "Image URL cannot exceed 500 characters"
    )
    private String imageUrl;
}