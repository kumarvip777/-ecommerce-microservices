package com.kumar.orderservice.service.impl;

import com.kumar.orderservice.client.ProductFeignClient;
import com.kumar.orderservice.dto.*;
import com.kumar.orderservice.dto.external.ProductResponse;
import com.kumar.orderservice.dto.external.StockUpdateRequestDTO;
import com.kumar.orderservice.entity.Order;
import com.kumar.orderservice.entity.OrderItem;
import com.kumar.orderservice.enums.OrderStatus;
import com.kumar.orderservice.exception.order.NoOrdersFoundException;
import com.kumar.orderservice.exception.order.OrderNotFoundException;
import com.kumar.orderservice.repo.OrderItemRepository;
import com.kumar.orderservice.repo.OrderRepository;
import com.kumar.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger logger =
            LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;

    private final ProductFeignClient productFeignClient;

    private final OrderItemRepository orderItemRepository;

    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderResponseDTO createOrder(
            @Valid OrderRequestDTO requestDTO) {

        logger.info("Creating Order");

        Order order = new Order();

        order.setUserId(requestDTO.getUserId());
        order.setAddressId(requestDTO.getAddressId());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(BigDecimal.ZERO);


        for (OrderItemRequestDTO item : requestDTO.getOrderItems()) {
            ProductResponse product =
                    productFeignClient.getProductById(item.getProductId()).getData();
            if (product == null) {
                throw new RuntimeException("Product not found");
            }


            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock available");
            }

            StockUpdateRequestDTO stockUpdateRequestDTO =
                    new StockUpdateRequestDTO();

            stockUpdateRequestDTO.setQuantity(item.getQuantity());

            productFeignClient.reduceStock(
                    product.getProductId(),
                    stockUpdateRequestDTO
            );


            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(product.getProductId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setSubTotal(
                    product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
            order.setTotalAmount(
                    order.getTotalAmount().add(orderItem.getSubTotal())
            );
            order.getOrderItems().add(orderItem);

        }


        Order savedOrder =
                orderRepository.save(order);

        logger.info(
                "Order Created Successfully : {}",
                savedOrder.getOrderId());

        return convertToResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long orderId) {

        logger.info("Fetching Order : {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id : " + orderId));

        return convertToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)

    public List<OrderResponseDTO> getOrdersByUserId(Long userId) {

        logger.info("Fetching Orders For User : {}", userId);

        List<Order> orders =
                orderRepository.findByUserId(userId);

        if (orders.isEmpty()) {
            throw new NoOrdersFoundException(
                    "No Orders Found For User Id : " + userId);
        }

        return orders.stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)

    public List<OrderResponseDTO> getOrdersByStatus(
            OrderStatus status) {

        logger.info("Fetching Orders By Status : {}", status);

        List<Order> orders =
                orderRepository.findByStatus(status);

        if (orders.isEmpty()) {
            throw new NoOrdersFoundException(
                    "No Orders Found");
        }

        return orders.stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrderStatus(
            Long orderId,
            @Valid UpdateOrderDTO updateDTO) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id : " + orderId));

        order.setStatus(updateDTO.getStatus());

        Order updatedOrder =
                orderRepository.save(order);

        return convertToResponse(updatedOrder);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id : " + orderId));

        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);
    }

    private OrderResponseDTO convertToResponse(Order order) {

        OrderResponseDTO dto =
                modelMapper.map(order, OrderResponseDTO.class);

        dto.setUserId(order.getUserId());

        dto.setAddressId(order.getAddressId());

        dto.setOrderItems(
                order.getOrderItems()
                        .stream()
                        .map(this::convertToOrderItemResponse)
                        .toList()
        );

        return dto;
    }

    private OrderItemResponseDTO convertToOrderItemResponse(
            OrderItem orderItem) {

        OrderItemResponseDTO dto =
                modelMapper.map(
                        orderItem,
                        OrderItemResponseDTO.class
                );

        dto.setProductId(orderItem.getProductId());

        // Temporary (Feign add பண்ணும்போது update பண்ணலாம்)
        dto.setProductName(null);
        dto.setBrand(null);

        return dto;
    }
}