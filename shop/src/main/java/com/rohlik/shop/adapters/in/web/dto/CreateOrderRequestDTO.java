package com.rohlik.shop.adapters.in.web.dto;

import java.util.List;

public record CreateOrderRequestDTO(
    List<CreateOrderItemRequestDTO> items
) {} 