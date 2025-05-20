package com.example.demo.integration;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.JwtResponse;
import com.example.demo.entities.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;

    @BeforeEach
    public void setup() throws Exception {
        // Login to obtain JWT token for authenticated requests
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin"); // Assuming this user exists in test DB
        loginRequest.setPassword("admin123");

        MvcResult result = mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JwtResponse jwtResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), JwtResponse.class);
        authToken = jwtResponse.getToken();
    }

    @Test
    public void testProductLifecycle() throws Exception {
        // Create a new product
        Product newProduct = new Product();
        newProduct.setName("Integration Test Product");
        newProduct.setDescription("Product created during integration test");
        newProduct.setPrice(new BigDecimal("99.99"));
        newProduct.setQuantity(50);
        
        MvcResult createResult = mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Integration Test Product")))
                .andExpect(jsonPath("$.price", is(99.99)))
                .andReturn();
        
        // Extract the ID of the newly created product
        Product createdProduct = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), Product.class);
        Long productId = createdProduct.getId();
        
        // Get the product by ID
        mockMvc.perform(get("/api/products/{id}", productId)
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(productId.intValue())))
                .andExpect(jsonPath("$.name", is("Integration Test Product")));
        
        // Update the product
        createdProduct.setName("Updated Integration Test Product");
        createdProduct.setPrice(new BigDecimal("149.99"));
        
        mockMvc.perform(put("/api/products/{id}", productId)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createdProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Integration Test Product")))
                .andExpect(jsonPath("$.price", is(149.99)));
        
        // Delete the product
        mockMvc.perform(delete("/api/products/{id}", productId)
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNoContent());
        
        // Verify the product is deleted
        mockMvc.perform(get("/api/products/{id}", productId)
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void testGetAllProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer " + authToken)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", notNullValue()))
                .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(0)));
    }
} 