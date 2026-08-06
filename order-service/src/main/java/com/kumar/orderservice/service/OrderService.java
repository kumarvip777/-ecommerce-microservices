package com.kumar.orderservice.service;
import com.kumar.orderservice.dto.OrderRequestDTO;
import com.kumar.orderservice.dto.OrderResponseDTO;
import com.kumar.orderservice.dto.UpdateOrderDTO;
import com.kumar.orderservice.enums.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderResponseDTO createOrder(OrderRequestDTO requestDTO);

    OrderResponseDTO getOrderById(Long orderId);

    List<OrderResponseDTO> getOrdersByUserId(Long userId);

    List<OrderResponseDTO> getOrdersByStatus(OrderStatus status);

    OrderResponseDTO updateOrderStatus(
            Long orderId,
            UpdateOrderDTO updateDTO
    );

    void cancelOrder(Long orderId);
}