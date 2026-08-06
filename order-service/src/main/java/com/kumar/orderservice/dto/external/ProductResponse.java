package com.kumar.orderservice.dto.external;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductResponse {

    private Long productId;

    private String productName;

    private BigDecimal price;

    private Integer stockQuantity;

}