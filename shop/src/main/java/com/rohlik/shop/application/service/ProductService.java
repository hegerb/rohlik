package com.rohlik.shop.application.service;

import com.rohlik.shop.adapters.out.persistence.JpaProductRepository;
import com.rohlik.shop.application.domain.OrderEntity;
import com.rohlik.shop.application.domain.OrderStatus;
import com.rohlik.shop.application.domain.ProductEntity;
import com.rohlik.shop.application.domain.OrderItemEntity;
import com.rohlik.shop.adapters.in.web.dto.ProductDTO;
import com.rohlik.shop.adapters.in.web.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final JpaProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        ProductEntity productEntity = productMapper.toEntity(productDTO);
        ProductEntity savedProduct = productRepository.save(productEntity);
        return productMapper.toDTO(savedProduct);
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
            .filter(ProductEntity::isActive)  // Filtrujeme pouze aktivní produkty
            .map(productMapper::toDTO)
            .collect(Collectors.toList());
    }

    public ProductDTO getProductById(Long id) {
        ProductEntity product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        
        if (!product.isActive()) {
            throw new IllegalArgumentException("Product with id: " + id + " is inactive");
        }
        
        return productMapper.toDTO(product);
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        // Kontrola, zda produkt existuje a je aktivní
        ProductEntity existingProduct = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
            
        if (!existingProduct.isActive()) {
            throw new IllegalArgumentException("Cannot update inactive product with id: " + id);
        }
        
        // Kontrola verze pro optimistic locking
        if (!Objects.equals(productDTO.version(), existingProduct.getVersion())) {
            throw new org.springframework.dao.OptimisticLockingFailureException("Product with id " + id + 
                " was updated by another transaction");
        }
        
        // 1. Nejprve deaktivujeme starý produkt
        existingProduct.setActive(false);
        productRepository.save(existingProduct);
        
        // 2. Vytvoříme nový produkt s aktualizovanými údaji, ale s novým ID
        ProductEntity newProduct = productMapper.toEntity(productDTO);
        newProduct.setId(null); // Zajistíme, že se vytvoří nová entita s novým ID
        newProduct.setActive(true);
        newProduct.setVersion(0L); // Resetujeme verzi na 0
        newProduct.setOrderItems(new ArrayList<>()); // Inicializujeme prázdný seznam orderItems
        
        ProductEntity savedNewProduct = productRepository.save(newProduct);
        
        // 3. Pro všechny aktivní objednávky aktualizujeme referenci na nový produkt
        List<OrderItemEntity> activeOrderItems = existingProduct.getOrderItems().stream()
            .filter(item -> item.getOrder().getStatus() == OrderStatus.PENDING)
            .collect(Collectors.toList());
        
        for (OrderItemEntity orderItem : activeOrderItems) {
            orderItem.setPrice(savedNewProduct.getPrice());
            orderItem.setProduct(savedNewProduct);
            savedNewProduct.getOrderItems().add(orderItem);
        }
        
        // 4. Odstraníme aktivní orderItems ze seznamu starého produktu
        existingProduct.getOrderItems().removeAll(activeOrderItems);
        
        // 5. Uložíme změny
        productRepository.save(existingProduct);
        productRepository.save(savedNewProduct);

        return productMapper.toDTO(savedNewProduct);
    }

    @Transactional
    public void deactivateProduct(Long id) {
        ProductEntity product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));

        // Kontrola, zda na produktu nezávisí nějaká aktivní objednávka (ve stavu PENDING)
        boolean hasActiveOrders = product.getOrderItems().stream()
                .map(item -> item.getOrder())
                .anyMatch(order -> order.getStatus() == OrderStatus.PENDING);

        // touto podmienkou mozeme menit logiku:
        // Delete a product – Remove a product (if no active orders depend on it).
        if (hasActiveOrders) {
            throw new IllegalStateException("Cannot deactivate product with ID " + id + " because it is referenced by active orders");
        }

        // Místo smazání pouze deaktivujeme produkt
        product.setActive(false);
        productRepository.save(product);    }
}