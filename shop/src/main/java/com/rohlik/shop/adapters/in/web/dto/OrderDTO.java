package com.rohlik.shop.adapters.in.web.dto;

import com.rohlik.shop.application.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDTO(
    Long id,
    LocalDateTime createdAt,
    LocalDateTime expiresAt,
    OrderStatus status,
    List<OrderItemDTO> items,
    Long version
) {} 