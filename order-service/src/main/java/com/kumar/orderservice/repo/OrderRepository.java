package com.kumar.orderservice.repo;

import com.kumar.orderservice.entity.Order;
import com.kumar.orderservice.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByUserIdAndStatus(
            Long userId,
            OrderStatus status
    );
}