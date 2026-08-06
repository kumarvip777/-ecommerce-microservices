package com.kumar.productservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateProductDTO {

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

    @Size(
            max = 100,
            message = "Brand cannot exceed 100 characters"
    )
    private String brand;

    @DecimalMin(
            value = "0.01",
            message = "Price must be greater than zero"
    )
    private BigDecimal price;

    @Min(
            value = 0,
            message = "Stock quantity cannot be negative"
    )
    private Integer stockQuantity;

    private Long categoryId;

    @Size(
            max = 500,
            message = "Image URL cannot exceed 500 characters"
    )
    private String imageUrl;

    public boolean isEmpty() {

        return productName == null
                && description == null
                && brand == null
                && price == null
                && stockQuantity == null
                && imageUrl == null
                && categoryId == null;
    }
}