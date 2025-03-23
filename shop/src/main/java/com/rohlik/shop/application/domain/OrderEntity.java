package com.rohlik.shop.application.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shop_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Created date is required")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Size(min = 1, message = "Order must contain at least one item")
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> items = new ArrayList<>();

    @Version
    private Long version;



    public void cancel() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be cancelled");
        }
        this.status = OrderStatus.CANCELLED;
        items.forEach(item -> item.getProduct().increaseStock(item.getQuantity()));
    }

    public void complete() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be completed");
        }
        this.status = OrderStatus.COMPLETED;
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public void addItem(OrderItemEntity item) {
        items.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItemEntity item) {
        items.remove(item);
        item.setOrder(null);
    }

    @Override
    public String toString() {
        return "OrderEntity{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                ", status=" + status +
                ", items=" + items +
                ", version=" + version +
                '}';
    }
}