package com.kumar.orderservice.controller;

import com.kumar.orderservice.dto.OrderRequestDTO;
import com.kumar.orderservice.dto.OrderResponseDTO;
import com.kumar.orderservice.dto.UpdateOrderDTO;
import com.kumar.orderservice.enums.OrderStatus;
import com.kumar.orderservice.payload.ApiResponse;
import com.kumar.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(
        name = "Order Management",
        description = "APIs for managing customer orders"
)
public class OrderController {

    private static final Logger logger =
            LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;
    @Operation(
            summary = "Create Order",
            description = "Creates a new order from cart"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(

            @Valid
            @RequestBody OrderRequestDTO requestDTO,

            HttpServletRequest request) {

        logger.info("Received request to create order");

        OrderResponseDTO responseDTO =
                orderService.createOrder(requestDTO);

        ApiResponse<OrderResponseDTO> response =
                ApiResponse.success(
                        responseDTO,
                        "Order created successfully",
                        request.getRequestURI()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @Operation(
            summary = "Get Order By Id"
    )
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrderById(

            @PathVariable Long orderId,

            HttpServletRequest request) {

        OrderResponseDTO responseDTO =
                orderService.getOrderById(orderId);

        ApiResponse<OrderResponseDTO> response =
                ApiResponse.success(
                        responseDTO,
                        "Order fetched successfully",
                        request.getRequestURI()
                );

        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Get Orders By User"
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getOrdersByUser(

            @PathVariable Long userId,

            HttpServletRequest request) {

        List<OrderResponseDTO> responseDTO =
                orderService.getOrdersByUserId(userId);

        ApiResponse<List<OrderResponseDTO>> response =
                ApiResponse.success(
                        responseDTO,
                        "Orders fetched successfully",
                        request.getRequestURI()
                );

        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Get Orders By Status"
    )
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getOrdersByStatus(

            @PathVariable OrderStatus status,

            HttpServletRequest request) {

        List<OrderResponseDTO> responseDTO =
                orderService.getOrdersByStatus(status);

        ApiResponse<List<OrderResponseDTO>> response =
                ApiResponse.success(
                        responseDTO,
                        "Orders fetched successfully",
                        request.getRequestURI()
                );

        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Update Order Status"
    )
    @PutMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> updateOrderStatus(

            @PathVariable Long orderId,

            @Valid
            @RequestBody UpdateOrderDTO updateDTO,

            HttpServletRequest request) {

        OrderResponseDTO responseDTO =
                orderService.updateOrderStatus(
                        orderId,
                        updateDTO
                );

        ApiResponse<OrderResponseDTO> response =
                ApiResponse.success(
                        responseDTO,
                        "Order updated successfully",
                        request.getRequestURI()
                );

        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Cancel Order"
    )
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<String>> cancelOrder(

            @PathVariable Long orderId,

            HttpServletRequest request) {

        orderService.cancelOrder(orderId);

        ApiResponse<String> response =
                ApiResponse.success(
                        "Order cancelled successfully",
                        "Order cancelled successfully",
                        request.getRequestURI()
                );

        return ResponseEntity.ok(response);
    }
}