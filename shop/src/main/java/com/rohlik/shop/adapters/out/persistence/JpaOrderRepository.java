package com.rohlik.shop.adapters.out.persistence;

import com.rohlik.shop.application.domain.OrderEntity;
import com.rohlik.shop.application.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaOrderRepository extends JpaRepository<OrderEntity, Long>  {
    List<OrderEntity> findByStatus(OrderStatus status);
} 