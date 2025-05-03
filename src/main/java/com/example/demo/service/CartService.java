package com.example.demo.service;

import com.example.demo.entities.Cart;
import com.example.demo.entities.Customer;
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
}
