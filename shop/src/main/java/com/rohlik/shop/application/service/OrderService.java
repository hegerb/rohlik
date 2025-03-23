package com.rohlik.shop.application.service;

import com.rohlik.shop.adapters.out.persistence.JpaOrderRepository;
import com.rohlik.shop.adapters.out.persistence.JpaProductRepository;
import com.rohlik.shop.application.domain.OrderEntity;
import com.rohlik.shop.application.domain.OrderItemEntity;
import com.rohlik.shop.application.domain.OrderStatus;
import com.rohlik.shop.adapters.in.web.dto.OrderDTO;
import com.rohlik.shop.adapters.in.web.mapper.OrderMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final JpaOrderRepository orderRepository;
    private final JpaProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        OrderEntity orderEntity = orderMapper.toEntity(orderDTO);
        // Validate stock for all items
        orderEntity.getItems().forEach(OrderItemEntity::validateStock);
        
        // Set creation and expiration time
        orderEntity.setCreatedAt(LocalDateTime.now());
        orderEntity.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        orderEntity.setStatus(OrderStatus.PENDING);
        
        // Decrease stock for all items
        orderEntity.getItems().forEach(item ->
            item.getProduct().decreaseStock(item.getQuantity())
        );
        
        OrderEntity savedOrder = orderRepository.save(orderEntity);
        return orderMapper.toDTO(savedOrder);
    }

    public List<OrderDTO> getAllOrders() {
        List<OrderEntity> all = orderRepository.findAll();
        return all.stream()
            .map(orderMapper::toDTO)
            .collect(Collectors.toList());
    }

    public OrderDTO getOrderById(Long id) throws EntityNotFoundException {
        return orderRepository.findById(id)
            .map(orderMapper::toDTO).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long id, OrderStatus status) {
        OrderEntity order = orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
        order.setStatus(status);
        OrderEntity savedOrder = orderRepository.save(order);
        return orderMapper.toDTO(savedOrder);
    }

    @Transactional
    public OrderDTO addItemToOrder(Long orderId, OrderItemEntity item) {
        OrderEntity order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
        order.getItems().add(item);
        OrderEntity savedOrder = orderRepository.save(order);
        return orderMapper.toDTO(savedOrder);
    }

    @Transactional
    public OrderDTO removeItemFromOrder(Long orderId, Long itemId) {
        OrderEntity order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
        order.getItems().removeIf(item -> item.getId().equals(itemId));
        OrderEntity savedOrder = orderRepository.save(order);
        return orderMapper.toDTO(savedOrder);
    }

    @Transactional
    public OrderDTO updateOrderExpiration(Long orderId, LocalDateTime newExpiration) {
        OrderEntity order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
        order.setExpiresAt(newExpiration);
        OrderEntity savedOrder = orderRepository.save(order);
        return orderMapper.toDTO(savedOrder);
    }

    @Transactional
    public void cancelOrder(Long id) {
        OrderEntity orderEntity = orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
        orderEntity.cancel();
        orderRepository.save(orderEntity);
    }

    @Transactional
    public void completeOrder(Long id) {
        OrderEntity orderEntity = orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
        orderEntity.complete();
        orderRepository.save(orderEntity);
    }

    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void cleanupExpiredOrders() {
        List<OrderEntity> pendingOrderEntities = orderRepository.findByStatus(OrderStatus.PENDING);
        pendingOrderEntities.stream()
                .filter(OrderEntity::isExpired)
                .forEach(order -> {
                    order.cancel();
                    orderRepository.save(order);
                });
    }
} 