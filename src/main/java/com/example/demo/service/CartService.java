package com.example.demo.service;

import com.example.demo.dto.CartDTO;
import com.example.demo.entities.Cart;
import com.example.demo.entities.Customer;
import com.example.demo.entities.CustomerOrder;
import com.example.demo.entities.Product;

import java.util.List;
import java.util.Optional;

public interface CartService {
    List<Cart> getAllCarts();
    Optional<Cart> getCartById(Integer id);
    List<Cart> getCartsByCustomer(Customer customer);
    List<Cart> getCartsByProduct(Product product);
    Optional<Cart> getCartByCustomerAndProduct(Customer customer, Product product);
    Cart saveCart(Cart cart);
    void deleteCart(Integer id);
    boolean existsById(Integer id);
    void updateCartQuantity(Integer cartId, Integer quantity);
    
    // Additional methods required by CartController
    
    // Find cart by ID (alias for getCartById)
    Optional<Cart> findById(Integer id);
    
    // Find cart by customer ID
    Optional<Cart> findByCustomerId(Integer customerId);
    
    // Create cart from DTO
    Cart createCartFromDTO(CartDTO cartDTO);
    
    // Add product to cart
    Cart addProductToCart(Integer customerId, Integer productId, Integer quantity);
    
    // Remove product from cart
    Cart removeProductFromCart(Integer cartId, Integer productId);
    
    // Update product quantity in cart
    Cart updateProductQuantity(Integer cartId, Integer productId, Integer quantity);
    
    // Clear customer's cart
    Cart clearCart(Integer customerId);
    
    // Process checkout and create order
    Integer checkout(Integer customerId);
}
