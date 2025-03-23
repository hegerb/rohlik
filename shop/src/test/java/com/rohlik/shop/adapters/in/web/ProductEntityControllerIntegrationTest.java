package com.rohlik.shop.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rohlik.shop.adapters.in.web.dto.ProductDTO;
import com.rohlik.shop.application.service.ProductService;
import com.rohlik.shop.config.JacksonConfig;
import com.rohlik.shop.config.SecurityConfig;
import com.rohlik.shop.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import({SecurityConfig.class, JacksonConfig.class})
class ProductEntityControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDTO testProductDTO;
    private UserDetails userDetails;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        testProductDTO = new ProductDTO(
            1L,
            "Test Product",
            new BigDecimal("10.0"),
            100,
            0L
        );

        // Setup security mocks
        userDetails = new User("testuser", "password", Collections.emptyList());
        jwtToken = "test.jwt.token";
        
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.extractUsername(anyString())).thenReturn("testuser");
        when(jwtService.isTokenValid(anyString(), any(UserDetails.class))).thenReturn(true);
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        when(productService.createProduct(any(ProductDTO.class))).thenReturn(testProductDTO);

        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testProductDTO.id()))
                .andExpect(jsonPath("$.name").value(testProductDTO.name()))
                .andExpect(jsonPath("$.price").value("10.0"))
                .andExpect(jsonPath("$.stockQuantity").value(testProductDTO.stockQuantity()));
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() throws Exception {
        when(productService.getAllProducts()).thenReturn(Arrays.asList(testProductDTO));

        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testProductDTO.id()))
                .andExpect(jsonPath("$[0].name").value(testProductDTO.name()))
                .andExpect(jsonPath("$[0].price").value("10.0"))
                .andExpect(jsonPath("$[0].stockQuantity").value(testProductDTO.stockQuantity()));
    }

    @Test
    void getProductById_ShouldReturnProduct() throws Exception {
        when(productService.getProductById(1L)).thenReturn(testProductDTO);

        mockMvc.perform(get("/api/products/1")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testProductDTO.id()))
                .andExpect(jsonPath("$.name").value(testProductDTO.name()))
                .andExpect(jsonPath("$.price").value("10.0"))
                .andExpect(jsonPath("$.stockQuantity").value(testProductDTO.stockQuantity()));
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct() throws Exception {
        when(productService.updateProduct(any(), any(ProductDTO.class))).thenReturn(testProductDTO);

        mockMvc.perform(put("/api/products/1")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testProductDTO.id()))
                .andExpect(jsonPath("$.name").value(testProductDTO.name()))
                .andExpect(jsonPath("$.price").value("10.0"))
                .andExpect(jsonPath("$.stockQuantity").value(testProductDTO.stockQuantity()));
    }

    @Test
    void deleteProduct_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/products/1")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }
}