package com.kumar.productservice.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCategoryDTO {

    @Size(
            min = 2,
            max = 100,
            message = "Category name must be between 2 and 100 characters"
    )
    private String categoryName;

    @Size(
            max = 500,
            message = "Description cannot exceed 500 characters"
    )
    private String description;

    public boolean isEmpty() {

        return categoryName == null
                && description == null;
    }
}