package com.rohlik.shop.adapters.in.web.dto;

import java.math.BigDecimal;

public record OrderItemDTO(
    Long id,
    Long productId,
    String productName,
    Integer quantity,
    BigDecimal price
) {} 