package com.rohlik.shop.application.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal price;

    public void validateStock() {
        if (quantity > product.getStockQuantity()) {
            throw new IllegalStateException("Insufficient stock for product: " + product.getName());
        }
    }

    @Override
    public String toString() {
        return "OrderItemEntity{" +
                "id=" + id +
                ", order=" + order.getId() +
                ", product=" + product.getName() +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}