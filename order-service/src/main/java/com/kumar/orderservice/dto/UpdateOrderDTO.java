package com.kumar.orderservice.dto;

import com.kumar.orderservice.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderDTO {

    @NotNull(message = "Order status is required")
    private OrderStatus status;

    public boolean isEmpty() {
        return status == null;
    }
}