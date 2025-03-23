package com.rohlik.shop.adapters.in.web;

import com.rohlik.shop.application.domain.OrderStatus;
import com.rohlik.shop.application.service.OrderService;
import com.rohlik.shop.adapters.in.web.dto.OrderDTO;
import com.rohlik.shop.adapters.in.web.dto.OrderItemDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        OrderItemDTO orderItemDTO = new OrderItemDTO(
            1L,
            1L,
            "Test Product",
            2,
            BigDecimal.valueOf(10.0)
        );

        orderDTO = new OrderDTO(
            1L,
            LocalDateTime.now(),
            LocalDateTime.now().plusHours(24),
            OrderStatus.PENDING,
            Arrays.asList(orderItemDTO),
            0L
        );
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() {
        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(orderDTO);

        ResponseEntity<OrderDTO> response = orderController.createOrder(orderDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(orderDTO.id(), response.getBody().id());
        verify(orderService).createOrder(any(OrderDTO.class));
    }

    @Test
    void getAllOrders_ShouldReturnListOfOrders() {
        List<OrderDTO> orders = Arrays.asList(orderDTO);
        when(orderService.getAllOrders()).thenReturn(orders);

        ResponseEntity<List<OrderDTO>> response = orderController.getAllOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(orderDTO.id(), response.getBody().get(0).id());
    }

    @Test
    void getOrderById_ShouldReturnOrder() {
        when(orderService.getOrderById(1L)).thenReturn(orderDTO);

        ResponseEntity<OrderDTO> response = orderController.getOrderById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(orderDTO.id(), response.getBody().id());
    }

    @Test
    void cancelOrder_ShouldCancelOrder() {
        doNothing().when(orderService).cancelOrder(1L);

        ResponseEntity<Void> response = orderController.cancelOrder(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderService).cancelOrder(1L);
    }

    @Test
    void completeOrder_ShouldCompleteOrder() {
        doNothing().when(orderService).completeOrder(1L);

        ResponseEntity<Void> response = orderController.completeOrder(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderService).completeOrder(1L);
    }
} 