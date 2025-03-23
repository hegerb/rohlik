package com.rohlik.shop.adapters.in.web.dto;

public record CreateOrderItemRequestDTO(
    Long productId,
    Integer quantity
) {} 