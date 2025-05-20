package com.example.demo.repositories;

import com.example.demo.entities.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testFindById() {
        // Act
        Optional<Product> productOptional = productRepository.findById(1L);
        
        // Assert
        assertTrue(productOptional.isPresent());
        assertEquals("Test Product 1", productOptional.get().getName());
    }

    @Test
    public void testFindByName() {
        // Act
        Optional<Product> productOptional = productRepository.findByName("Test Product 2");
        
        // Assert
        assertTrue(productOptional.isPresent());
        assertEquals(new BigDecimal("149.99"), productOptional.get().getPrice());
    }

    @Test
    public void testFindAll() {
        // Act
        List<Product> products = productRepository.findAll();
        
        // Assert
        assertFalse(products.isEmpty());
        assertEquals(3, products.size());
    }

    @Test
    public void testFindAllPaginated() {
        // Act
        Page<Product> productPage = productRepository.findAll(PageRequest.of(0, 2, Sort.by("price").ascending()));
        
        // Assert
        assertEquals(2, productPage.getContent().size());
        assertEquals(3, productPage.getTotalElements());
        assertEquals("Test Product 1", productPage.getContent().get(0).getName());
    }

    @Test
    public void testFindByPriceRange() {
        // Act
        List<Product> products = productRepository.findByPriceBetween(
                new BigDecimal("100.00"), new BigDecimal("200.00"));
        
        // Assert
        assertEquals(2, products.size());
        assertTrue(products.stream().anyMatch(p -> p.getName().equals("Test Product 2")));
        assertTrue(products.stream().anyMatch(p -> p.getName().equals("Test Product 3")));
    }

    @Test
    public void testFindByPriceLessThan() {
        // Act
        List<Product> products = productRepository.findByPriceLessThan(new BigDecimal("150.00"));
        
        // Assert
        assertEquals(1, products.size());
        assertEquals("Test Product 1", products.get(0).getName());
    }

    @Test
    public void testSaveProduct() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setName("New Test Product");
        newProduct.setDescription("Description for new test product");
        newProduct.setPrice(new BigDecimal("299.99"));
        newProduct.setQuantity(10);
        
        // Act
        Product savedProduct = productRepository.save(newProduct);
        
        // Assert
        assertNotNull(savedProduct.getId());
        assertEquals("New Test Product", savedProduct.getName());
        
        // Verify it was actually saved
        Optional<Product> retrievedProduct = productRepository.findById(savedProduct.getId());
        assertTrue(retrievedProduct.isPresent());
    }

    @Test
    public void testDeleteProduct() {
        // Arrange
        long initialCount = productRepository.count();
        
        // Act
        productRepository.deleteById(1L);
        
        // Assert
        assertEquals(initialCount - 1, productRepository.count());
        assertFalse(productRepository.findById(1L).isPresent());
    }
} 