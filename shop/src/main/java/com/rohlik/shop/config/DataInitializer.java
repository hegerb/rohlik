package com.rohlik.shop.config;

import com.rohlik.shop.application.domain.OrderEntity;
import com.rohlik.shop.application.domain.OrderItemEntity;
import com.rohlik.shop.application.domain.OrderStatus;
import com.rohlik.shop.application.domain.ProductEntity;
import com.rohlik.shop.application.domain.UserEntity;
import com.rohlik.shop.adapters.out.persistence.JpaOrderRepository;
import com.rohlik.shop.adapters.out.persistence.JpaProductRepository;
import com.rohlik.shop.adapters.out.persistence.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final JpaProductRepository productRepository;
    private final JpaUserRepository userRepository;
    private final JpaOrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Initialize test user
        if (userRepository.findByUsername("testuser").isEmpty()) {
            UserEntity testUser = new UserEntity();
            testUser.setUsername("testuser");
            testUser.setPassword(passwordEncoder.encode("testpass"));
            testUser.setRole("USER");
            userRepository.save(testUser);
        }

        // Initialize grocery products
        if (productRepository.count() == 0) {
            List<ProductEntity> products = Arrays.asList(
                createProduct("Mléko", new BigDecimal("29.90"), 50),
                createProduct("Chléb", new BigDecimal("39.90"), 30),
                createProduct("Vejce 10ks", new BigDecimal("49.90"), 40),
                createProduct("Máslo", new BigDecimal("69.90"), 25),
                createProduct("Sýr Eidam", new BigDecimal("89.90"), 20),
                createProduct("Jablka", new BigDecimal("29.90"), 100),
                createProduct("Banány", new BigDecimal("39.90"), 80),
                createProduct("Brambory", new BigDecimal("19.90"), 200),
                createProduct("Cibule", new BigDecimal("15.90"), 150),
                createProduct("Rajčata", new BigDecimal("49.90"), 60),
                createProduct("Okurky", new BigDecimal("19.90"), 70),
                createProduct("Papriky", new BigDecimal("39.90"), 50),
                createProduct("Těstoviny", new BigDecimal("29.90"), 100),
                createProduct("Rýže", new BigDecimal("49.90"), 80),
                createProduct("Olej", new BigDecimal("89.90"), 40)
            );
            productRepository.saveAll(products);
        }

        // Initialize some test orders
        if (orderRepository.count() == 0) {
            // Pending order
            OrderEntity pendingOrder = createOrder(OrderStatus.PENDING);
            pendingOrder.addItem(createOrderItem(productRepository.findByName("Mléko").get(), 2));
            pendingOrder.addItem(createOrderItem(productRepository.findByName("Chléb").get(), 1));
            pendingOrder.addItem(createOrderItem(productRepository.findByName("Vejce 10ks").get(), 1));
            orderRepository.save(pendingOrder);

            // Completed order
            OrderEntity completedOrder = createOrder(OrderStatus.COMPLETED);
            completedOrder.addItem(createOrderItem(productRepository.findByName("Máslo").get(), 2));
            completedOrder.addItem(createOrderItem(productRepository.findByName("Sýr Eidam").get(), 1));
            completedOrder.addItem(createOrderItem(productRepository.findByName("Jablka").get(), 3));
            orderRepository.save(completedOrder);

            // Cancelled order
            OrderEntity cancelledOrder = createOrder(OrderStatus.CANCELLED);
            cancelledOrder.addItem(createOrderItem(productRepository.findByName("Banány").get(), 2));
            cancelledOrder.addItem(createOrderItem(productRepository.findByName("Brambory").get(), 5));
            orderRepository.save(cancelledOrder);
        }
    }

    private ProductEntity createProduct(String name, BigDecimal price, Integer stockQuantity) {
        ProductEntity product = new ProductEntity();
        product.setName(name);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        return product;
    }

    private OrderEntity createOrder(OrderStatus status) {
        OrderEntity order = new OrderEntity();
        order.setCreatedAt(LocalDateTime.now());
        order.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        order.setStatus(status);
        return order;
    }

    private OrderItemEntity createOrderItem(ProductEntity product, Integer quantity) {
        OrderItemEntity item = new OrderItemEntity();
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setPrice(product.getPrice());
        return item;
    }
} 