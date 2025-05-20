package com.example.demo.util;

import com.example.demo.entities.*;
import com.example.demo.dto.*;
import com.example.demo.security.services.UserDetailsImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Factory class for creating test data objects
 */
public class TestDataFactory {
    
    public static User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEnabled(true);
        return user;
    }
    
    public static UserDetailsImpl createUserDetails() {
        User user = createTestUser();
        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setName("ROLE_USER");
        roles.add(role);
        user.setRoles(roles);
        
        return UserDetailsImpl.build(user);
    }
    
    public static UserDetailsImpl createAdminUserDetails() {
        User user = createTestUser();
        user.setUsername("admin");
        user.setEmail("admin@example.com");
        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        roles.add(role);
        user.setRoles(roles);
        
        return UserDetailsImpl.build(user);
    }
    
    public static Product createTestProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test description for product");
        product.setPrice(new BigDecimal("99.99"));
        product.setQuantity(100);
        product.setImageUrl("https://example.com/image.jpg");
        return product;
    }
    
    public static List<Product> createTestProducts(int count) {
        List<Product> products = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Product product = new Product();
            product.setId((long) i);
            product.setName("Test Product " + i);
            product.setDescription("Test description for product " + i);
            product.setPrice(new BigDecimal(10 * i + ".99"));
            product.setQuantity(100);
            product.setImageUrl("https://example.com/image" + i + ".jpg");
            products.add(product);
        }
        return products;
    }
    
    public static Order createTestOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalPrice(new BigDecimal("199.98"));
        order.setStatus("PENDING");
        order.setUser(createTestUser());
        return order;
    }
    
    public static OrderDetail createTestOrderDetail() {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setId(1L);
        orderDetail.setOrder(createTestOrder());
        orderDetail.setProduct(createTestProduct());
        orderDetail.setQuantity(2);
        orderDetail.setUnitPrice(new BigDecimal("99.99"));
        return orderDetail;
    }
    
    public static Cart createTestCart() {
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(createTestUser());
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        return cart;
    }
    
    public static CartItem createTestCartItem() {
        CartItem item = new CartItem();
        item.setId(1L);
        item.setCart(createTestCart());
        item.setProduct(createTestProduct());
        item.setQuantity(1);
        return item;
    }
    
    public static Review createTestReview() {
        Review review = new Review();
        review.setId(1L);
        review.setProduct(createTestProduct());
        review.setUser(createTestUser());
        review.setRating(5);
        review.setComment("Great product!");
        review.setCreatedAt(LocalDateTime.now());
        return review;
    }
} 