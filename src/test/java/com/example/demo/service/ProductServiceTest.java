package com.example.demo.service;

import com.example.demo.dto.ProductDTO;
import com.example.demo.entities.Product;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.service.impl.ProductServiceImpl;
import com.example.demo.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private List<Product> testProducts;

    @BeforeEach
    public void setup() {
        testProduct = TestDataFactory.createTestProduct();
        testProducts = TestDataFactory.createTestProducts(5);
    }

    @Test
    public void testGetAllProducts() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(testProducts, pageable, testProducts.size());
        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);

        // Act
        Page<Product> result = productService.getAllProducts(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getTotalElements());
        assertEquals(testProducts.get(0).getName(), result.getContent().get(0).getName());
        verify(productRepository).findAll(pageable);
    }

    @Test
    public void testGetProductById_Found() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));

        // Act
        Product result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testProduct.getId(), result.getId());
        assertEquals(testProduct.getName(), result.getName());
        verify(productRepository).findById(1L);
    }

    @Test
    public void testGetProductById_NotFound() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> productService.getProductById(999L));
        verify(productRepository).findById(999L);
    }

    @Test
    public void testCreateProduct() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // Act
        Product result = productService.createProduct(testProduct);
        
        // Assert
        assertNotNull(result);
        assertEquals(testProduct.getName(), result.getName());
        assertEquals(testProduct.getPrice(), result.getPrice());
        verify(productRepository).save(testProduct);
    }

    @Test
    public void testUpdateProduct_Success() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        testProduct.setName("Updated Product Name");
        testProduct.setPrice(new BigDecimal("149.99"));
        
        // Act
        Product result = productService.updateProduct(1L, testProduct);
        
        // Assert
        assertNotNull(result);
        assertEquals("Updated Product Name", result.getName());
        assertEquals(new BigDecimal("149.99"), result.getPrice());
        verify(productRepository).findById(1L);
        verify(productRepository).save(testProduct);
    }

    @Test
    public void testDeleteProduct() {
        // Arrange
        doNothing().when(productRepository).deleteById(anyLong());
        when(productRepository.existsById(anyLong())).thenReturn(true);
        
        // Act
        productService.deleteProduct(1L);
        
        // Assert
        verify(productRepository).deleteById(1L);
    }

    @Test
    public void testDeleteProduct_NotFound() {
        // Arrange
        when(productRepository.existsById(anyLong())).thenReturn(false);
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> productService.deleteProduct(999L));
        verify(productRepository, never()).deleteById(anyLong());
    }
} 