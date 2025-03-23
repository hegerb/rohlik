package com.rohlik.shop.adapters.in.web;

import com.rohlik.shop.application.service.ProductService;
import com.rohlik.shop.adapters.in.web.dto.ProductDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        productDTO = new ProductDTO(
            1L,
            "Test Product",
            BigDecimal.valueOf(10.0),
            100,
            0L
        );
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() {
        when(productService.createProduct(any(ProductDTO.class))).thenReturn(productDTO);

        ResponseEntity<ProductDTO> response = productController.createProduct(productDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(productDTO.id(), response.getBody().id());
        verify(productService).createProduct(any(ProductDTO.class));
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() {
        List<ProductDTO> products = Arrays.asList(productDTO);
        when(productService.getAllProducts()).thenReturn(products);

        ResponseEntity<List<ProductDTO>> response = productController.getAllProducts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(productDTO.id(), response.getBody().get(0).id());
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        when(productService.getProductById(1L)).thenReturn(productDTO);

        ResponseEntity<ProductDTO> response = productController.getProductById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(productDTO.id(), response.getBody().id());
    }

    @Test
    void getProductById_WhenProductDoesNotExist_ShouldReturnNotFound() {
        when(productService.getProductById(1L)).thenReturn(null);

        ResponseEntity<ProductDTO> response = productController.getProductById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updateProduct_ShouldUpdateAndReturnProduct() {
        when(productService.updateProduct(eq(1L), any(ProductDTO.class))).thenReturn(productDTO);

        ResponseEntity<ProductDTO> response = productController.updateProduct(1L, productDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(productDTO.id(), response.getBody().id());
        verify(productService).updateProduct(eq(1L), any(ProductDTO.class));
    }

    @Test
    void deactivateProduct_ShouldDeactivateProduct() {
        doNothing().when(productService).deactivateProduct(1L);

        ResponseEntity<Void> response = productController.deactivateProduct(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).deactivateProduct(1L);
    }
} 