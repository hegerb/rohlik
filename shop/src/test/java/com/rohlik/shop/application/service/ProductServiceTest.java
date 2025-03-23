package com.rohlik.shop.application.service;

import com.rohlik.shop.adapters.in.web.dto.ProductDTO;
import com.rohlik.shop.adapters.out.persistence.JpaProductRepository;
import com.rohlik.shop.application.domain.OrderEntity;
import com.rohlik.shop.application.domain.OrderItemEntity;
import com.rohlik.shop.application.domain.OrderStatus;
import com.rohlik.shop.application.domain.ProductEntity;
import com.rohlik.shop.adapters.in.web.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private JpaProductRepository productRepository = Mockito.mock(JpaProductRepository.class);

    private ProductMapper productMapper = new ProductMapper();

    private ProductService productService = new ProductService(productRepository, productMapper);

    private ProductDTO productDTO;
    private ProductEntity productEntity;
    private ProductEntity inactiveProductEntity;

    @BeforeEach
    void setUp() {
        productDTO = new ProductDTO(
            1L,
            "Test Product",
            new BigDecimal("10.00"),
            100,
            0L
        );

        productEntity = new ProductEntity();
        productEntity.setId(1L);
        productEntity.setName("Test Product");
        productEntity.setPrice(new BigDecimal("10.00"));
        productEntity.setStockQuantity(100);
        productEntity.setVersion(0L);
        productEntity.setActive(true);
        productEntity.setOrderItems(new ArrayList<>());

        inactiveProductEntity = new ProductEntity();
        inactiveProductEntity.setId(2L);
        inactiveProductEntity.setName("Inactive Product");
        inactiveProductEntity.setPrice(new BigDecimal("20.00"));
        inactiveProductEntity.setStockQuantity(50);
        inactiveProductEntity.setVersion(0L);
        inactiveProductEntity.setActive(false);
        inactiveProductEntity.setOrderItems(new ArrayList<>());
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() {
        when(productRepository.save(any(ProductEntity.class))).thenReturn(productEntity);

        ProductDTO result = productService.createProduct(productDTO);

        assertNotNull(result);
        assertEquals(productDTO.id(), result.id());
        assertEquals(productDTO.name(), result.name());
        assertEquals(productDTO.price(), result.price());
        assertEquals(productDTO.stockQuantity(), result.stockQuantity());
        verify(productRepository).save(any(ProductEntity.class));
    }

    @Test
    void getAllProducts_ShouldReturnOnlyActiveProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(productEntity, inactiveProductEntity));

        var result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(productDTO.id(), result.get(0).id());
        assertEquals(productDTO.name(), result.get(0).name());
        assertEquals(productDTO.price(), result.get(0).price());
        assertEquals(productDTO.stockQuantity(), result.get(0).stockQuantity());
        verify(productRepository).findAll();
    }

    @Test
    void getProductById_ShouldReturnActiveProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));

        ProductDTO result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(productDTO.id(), result.id());
        assertEquals(productDTO.name(), result.name());
        assertEquals(productDTO.price(), result.price());
        assertEquals(productDTO.stockQuantity(), result.stockQuantity());
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductById_WhenProductIsInactive_ShouldThrowException() {
        when(productRepository.findById(2L)).thenReturn(Optional.of(inactiveProductEntity));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            productService.getProductById(2L)
        );

        assertEquals("Product with id: 2 is inactive", exception.getMessage());
        verify(productRepository).findById(2L);
    }

    @Test
    void getProductById_WhenProductDoesNotExist_ShouldThrowException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            productService.getProductById(1L)
        );

        assertEquals("Product not found with id: 1", exception.getMessage());
        verify(productRepository).findById(1L);
    }

    @Test
    void updateProduct_ShouldUpdateAndReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(productEntity);

        ProductDTO result = productService.updateProduct(1L, productDTO);

        assertNotNull(result);
        assertEquals(productDTO.id(), result.id());
        assertEquals(productDTO.name(), result.name());
        assertEquals(productDTO.price(), result.price());
        assertEquals(productDTO.stockQuantity(), result.stockQuantity());
        verify(productRepository).findById(1L);
        verify(productRepository, times(4)).save(any(ProductEntity.class));
    }

    @Test
    void updateProduct_WhenProductIsInactive_ShouldThrowException() {
        when(productRepository.findById(2L)).thenReturn(Optional.of(inactiveProductEntity));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            productService.updateProduct(2L, productDTO)
        );

        assertEquals("Cannot update inactive product with id: 2", exception.getMessage());
        verify(productRepository).findById(2L);
        verify(productRepository, never()).save(any(ProductEntity.class));
    }

    @Test
    void updateProduct_WhenProductDoesNotExist_ShouldThrowException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            productService.updateProduct(1L, productDTO)
        );

        assertEquals("Product not found with id: 1", exception.getMessage());
        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any(ProductEntity.class));
    }

    @Test
    void deactivateProduct_ShouldDeactivateProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
        
        productService.deactivateProduct(1L);
        
        verify(productRepository).findById(1L);
        verify(productRepository).save(productEntity);
        assertFalse(productEntity.isActive());
    }
    
    @Test
    void deactivateProduct_WhenProductHasActiveOrders_ShouldThrowException() {
        // Příprava dat pro test
        OrderEntity pendingOrder = new OrderEntity();
        pendingOrder.setStatus(OrderStatus.PENDING);
        
        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setOrder(pendingOrder);
        
        productEntity.setOrderItems(Collections.singletonList(orderItem));
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> 
            productService.deactivateProduct(1L)
        );
        
        assertEquals("Cannot deactivate product with ID 1 because it is referenced by active orders", exception.getMessage());
        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any(ProductEntity.class));
    }
    
    @Test
    void updateProductVersion_ShouldCreateNewVersionAndUpdateActiveOrderReferences() {
        // Příprava dat pro test
        ProductEntity savedNewProduct = new ProductEntity();
        savedNewProduct.setId(3L); // Nové ID pro novou verzi produktu
        savedNewProduct.setName("Updated Product");
        savedNewProduct.setPrice(new BigDecimal("15.00"));
        savedNewProduct.setStockQuantity(150);
        savedNewProduct.setVersion(0L);
        savedNewProduct.setActive(true);
        savedNewProduct.setOrderItems(new ArrayList<>());
        
        // Vytvoření aktivní objednávky
        OrderEntity pendingOrder = new OrderEntity();
        pendingOrder.setStatus(OrderStatus.PENDING);
        
        // Vytvoření položky objednávky odkazující na starý produkt
        OrderItemEntity activeOrderItem = new OrderItemEntity();
        activeOrderItem.setOrder(pendingOrder);
        activeOrderItem.setProduct(productEntity);
        
        // Přidání položky do kolekce starého produktu - použijeme ArrayList místo Collections.singletonList
        ArrayList<OrderItemEntity> orderItems = new ArrayList<>();
        orderItems.add(activeOrderItem);
        productEntity.setOrderItems(orderItems);
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
        when(productRepository.save(any(ProductEntity.class)))
            .thenReturn(productEntity) // První volání - deaktivace starého produktu
            .thenReturn(savedNewProduct) // Druhé volání - uložení nového produktu
            .thenReturn(productEntity) // Třetí volání - aktualizace starého produktu
            .thenReturn(savedNewProduct); // Čtvrté volání - aktualizace nového produktu
            
        ProductDTO updatedProductDTO = new ProductDTO(
            1L,
            "Updated Product",
            new BigDecimal("15.00"),
            150,
            0L
        );
        
        // Spuštění testované metody
        ProductDTO result = productService.updateProduct(1L, updatedProductDTO);
        
        // Ověření výsledků
        assertNotNull(result);
        assertEquals(savedNewProduct.getId(), result.id());
        assertEquals(updatedProductDTO.name(), result.name());
        assertEquals(updatedProductDTO.price(), result.price());
        assertEquals(updatedProductDTO.stockQuantity(), result.stockQuantity());
        
        // Ověření, že starý produkt byl deaktivován
        assertFalse(productEntity.isActive());
        
        // Ověření, že reference na aktivní objednávku byly přesunuty na nový produkt
        assertTrue(productEntity.getOrderItems().isEmpty());
        assertEquals(1, savedNewProduct.getOrderItems().size());
        assertSame(activeOrderItem, savedNewProduct.getOrderItems().get(0));
        assertSame(savedNewProduct, activeOrderItem.getProduct());
        
        // Ověření, že repository bylo voláno správným způsobem
        verify(productRepository).findById(1L);
        verify(productRepository, times(4)).save(any(ProductEntity.class));
    }
} 