package com.kumar.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemResponseDTO {

    private Long orderItemId;

    private Long productId;

    private String productName;

    private String brand;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal subTotal;

}