package com.rohlik.shop.adapters.in.web.mapper;

import com.rohlik.shop.application.domain.OrderItemEntity;
import com.rohlik.shop.adapters.in.web.dto.OrderItemDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderItemMapper {
    
    public OrderItemDTO toDTO(OrderItemEntity entity) {
        return new OrderItemDTO(
            entity.getId(),
            entity.getProduct().getId(),
            entity.getProduct().getName(),
            entity.getQuantity(),
            entity.getPrice()
        );
    }

    public OrderItemEntity toEntity(OrderItemDTO dto) {
        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(dto.id());
        entity.setQuantity(dto.quantity());
        entity.setPrice(dto.price());
        return entity;
    }
} 