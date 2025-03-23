package com.rohlik.shop.adapters.in.web.mapper;

import com.rohlik.shop.application.domain.ProductEntity;
import com.rohlik.shop.adapters.in.web.dto.ProductDTO;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    
    public ProductDTO toDTO(ProductEntity entity) {
        return new ProductDTO(
            entity.getId(),
            entity.getName(),
            entity.getPrice(),
            entity.getStockQuantity(),
            entity.getVersion()
        );
    }

    public ProductEntity toEntity(ProductDTO dto) {
        ProductEntity entity = new ProductEntity();
        entity.setId(dto.id());
        entity.setName(dto.name());
        entity.setPrice(dto.price());
        entity.setStockQuantity(dto.stockQuantity());
        entity.setVersion(dto.version());
        return entity;
    }
} 