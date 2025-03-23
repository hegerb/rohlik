package com.rohlik.shop.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rohlik.shop.adapters.in.web.dto.OrderDTO;
import com.rohlik.shop.adapters.in.web.dto.OrderItemDTO;
import com.rohlik.shop.application.domain.OrderStatus;
import com.rohlik.shop.application.service.OrderService;
import com.rohlik.shop.config.SecurityConfig;
import com.rohlik.shop.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(SecurityConfig.class)
class OrderEntityControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderDTO testOrderDTO;
    private OrderItemDTO testOrderItemDTO;
    private UserDetails userDetails;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        testOrderItemDTO = new OrderItemDTO(
            1L,
            1L,
            "Test Product",
            2,
            BigDecimal.valueOf(10.0)
        );

        testOrderDTO = new OrderDTO(
            1L,
            LocalDateTime.now(),
            LocalDateTime.now().plusMinutes(30),
            OrderStatus.PENDING,
            Arrays.asList(testOrderItemDTO),
            0L
        );

        // Setup security mocks
        userDetails = new User("testuser", "password", Collections.emptyList());
        jwtToken = "test.jwt.token";
        
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.extractUsername(anyString())).thenReturn("testuser");
        when(jwtService.isTokenValid(anyString(), any(UserDetails.class))).thenReturn(true);
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() throws Exception {
        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(testOrderDTO);

        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrderDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testOrderDTO.id()))
                .andExpect(jsonPath("$.status").value(testOrderDTO.status().toString()))
                .andExpect(jsonPath("$.items[0].id").value(testOrderItemDTO.id()))
                .andExpect(jsonPath("$.items[0].quantity").value(testOrderItemDTO.quantity()));
    }

    @Test
    void getAllOrders_ShouldReturnListOfOrders() throws Exception {
        when(orderService.getAllOrders()).thenReturn(Arrays.asList(testOrderDTO));

        mockMvc.perform(get("/api/orders")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testOrderDTO.id()))
                .andExpect(jsonPath("$[0].status").value(testOrderDTO.status().toString()))
                .andExpect(jsonPath("$[0].items[0].id").value(testOrderItemDTO.id()));
    }

    @Test
    void getOrderById_ShouldReturnOrder() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(testOrderDTO);

        mockMvc.perform(get("/api/orders/1")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testOrderDTO.id()))
                .andExpect(jsonPath("$.status").value(testOrderDTO.status().toString()))
                .andExpect(jsonPath("$.items[0].id").value(testOrderItemDTO.id()));
    }

    @Test
    void cancelOrder_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/orders/1/cancel")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    void completeOrder_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/orders/1/complete")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

}