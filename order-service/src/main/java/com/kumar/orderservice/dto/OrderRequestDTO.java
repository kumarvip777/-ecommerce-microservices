package com.kumar.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequestDTO {

    @NotNull(message = "User id is required")
    private Long userId;

    @NotNull(message = "Address id is required")
    private Long addressId;

    @Valid
    @NotNull(message = "Order items are required")
    private List<OrderItemRequestDTO> orderItems;

}