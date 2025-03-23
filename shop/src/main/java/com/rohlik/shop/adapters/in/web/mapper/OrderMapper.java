package com.rohlik.shop.adapters.in.web.mapper;

import com.rohlik.shop.application.domain.OrderEntity;
import com.rohlik.shop.adapters.in.web.dto.OrderDTO;
import com.rohlik.shop.adapters.in.web.dto.OrderItemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    private final OrderItemMapper orderItemMapper;

    public OrderDTO toDTO(OrderEntity entity) {
        List<OrderItemDTO> items = entity.getItems().stream()
            .map(orderItemMapper::toDTO)
            .collect(Collectors.toList());

        return new OrderDTO(
            entity.getId(),
            entity.getCreatedAt(),
            entity.getExpiresAt(),
            entity.getStatus(),
            items,
            entity.getVersion()
        );
    }

    public OrderEntity toEntity(OrderDTO dto) {
        OrderEntity entity = new OrderEntity();
        entity.setId(dto.id());
        entity.setCreatedAt(dto.createdAt());
        entity.setExpiresAt(dto.expiresAt());
        entity.setStatus(dto.status());
        entity.setVersion(dto.version());
        return entity;
    }
} 