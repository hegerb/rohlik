package com.rohlik.shop.application.service;

import com.rohlik.shop.adapters.in.web.dto.OrderDTO;
import com.rohlik.shop.adapters.in.web.dto.OrderItemDTO;
import com.rohlik.shop.adapters.out.persistence.JpaOrderRepository;
import com.rohlik.shop.adapters.out.persistence.JpaProductRepository;
import com.rohlik.shop.application.domain.OrderEntity;
import com.rohlik.shop.application.domain.OrderItemEntity;
import com.rohlik.shop.application.domain.OrderStatus;
import com.rohlik.shop.application.domain.ProductEntity;
import com.rohlik.shop.adapters.in.web.mapper.OrderMapper;
import com.rohlik.shop.adapters.in.web.mapper.OrderItemMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private JpaOrderRepository orderRepository = Mockito.mock(JpaOrderRepository.class);
    private JpaProductRepository productRepository = Mockito.mock(JpaProductRepository.class);
    private OrderItemMapper orderItemMapper = new OrderItemMapper();
    private OrderMapper orderMapper = new OrderMapper(orderItemMapper);
    private OrderService orderService = new OrderService(orderRepository, productRepository, orderMapper);

    private OrderDTO orderDTO;
    private OrderEntity orderEntity;
    private ProductEntity productEntity;
    private OrderItemEntity orderItemEntity;

    @BeforeEach
    void setUp() {
        productEntity = new ProductEntity();
        productEntity.setId(1L);
        productEntity.setName("Test Product");
        productEntity.setPrice(new BigDecimal("10.00"));
        productEntity.setStockQuantity(100);
        productEntity.setVersion(0L);

        orderItemEntity = new OrderItemEntity();
        orderItemEntity.setId(1L);
        orderItemEntity.setProduct(productEntity);
        orderItemEntity.setQuantity(2);
        orderItemEntity.setPrice(new BigDecimal("10.0"));

        orderEntity = new OrderEntity();
        orderEntity.setId(1L);
        orderEntity.setCreatedAt(LocalDateTime.now());
        orderEntity.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        orderEntity.setStatus(OrderStatus.PENDING);
        orderEntity.setItems(Arrays.asList(orderItemEntity));
        orderEntity.setVersion(0L);

        orderDTO = new OrderDTO(
            1L,
            orderEntity.getCreatedAt(),
            orderEntity.getExpiresAt(),
            OrderStatus.PENDING,
            Arrays.asList(new OrderItemDTO(1L, 1L, "Test Product", 2, new BigDecimal("10.00"))),
            0L
        );
    }

    @Test
    void createOrder_ShouldCreateAndReturnOrder() {
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);
        when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));

        OrderDTO result = orderService.createOrder(orderDTO);

        assertNotNull(result);
        assertEquals(orderDTO.id(), result.id());
        assertEquals(orderDTO.status(), result.status());
        assertEquals(1, result.items().size());
        assertEquals(orderDTO.items().get(0).quantity(), result.items().get(0).quantity());
        verify(orderRepository).save(any(OrderEntity.class));
    }

    @Test
    void getAllOrders_ShouldReturnListOfOrders() {
        when(orderRepository.findAll()).thenReturn(Arrays.asList(orderEntity));

        var result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderDTO.id(), result.get(0).id());
        assertEquals(orderDTO.status(), result.get(0).status());
        assertEquals(1, result.get(0).items().size());
        verify(orderRepository).findAll();
    }

    @Test
    void getOrderById_ShouldReturnOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));

        OrderDTO result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(orderDTO.id(), result.id());
        assertEquals(orderDTO.status(), result.status());
        assertEquals(1, result.items().size());
        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrderById_WhenOrderDoesNotExist_ShouldThrowException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> 
            orderService.getOrderById(1L)
        );

        verify(orderRepository).findById(1L);
    }

    @Test
    void updateOrderStatus_ShouldUpdateAndReturnOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);

        OrderDTO result = orderService.updateOrderStatus(1L, OrderStatus.COMPLETED);

        assertNotNull(result);
        assertEquals(orderDTO.id(), result.id());
        assertEquals(OrderStatus.COMPLETED, result.status());
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(OrderEntity.class));
    }

    @Test
    void updateOrderStatus_WhenOrderDoesNotExist_ShouldThrowException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            orderService.updateOrderStatus(1L, OrderStatus.COMPLETED)
        );

        assertEquals("Order not found with id: 1", exception.getMessage());
        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).save(any(OrderEntity.class));
    }

    @Test
    void cancelOrder_ShouldDeleteOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));
        doNothing().when(orderRepository).deleteById(1L);

        orderService.cancelOrder(1L);

        verify(orderRepository).findById(1L);
    }

    @Test
    void cancelOrder_WhenOrderDoesNotExist_ShouldThrowException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            orderService.cancelOrder(1L)
        );

        assertEquals("Order not found with id: 1", exception.getMessage());
        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).deleteById(anyLong());
    }
} 