package com.kumar.productservice.dto;

import com.kumar.productservice.entity.enums.CategoryStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryResponseDTO {

    private Long categoryId;

    private String categoryName;

    private String description;

    private CategoryStatus status;
}