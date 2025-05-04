package com.fitted.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByUserId(Long userId);
}
