package com.rohlik.shop.adapters.in.web.dto;

import java.math.BigDecimal;

public record ProductDTO(
    Long id,
    String name,
    BigDecimal price,
    Integer stockQuantity,
    Long version
) {} 