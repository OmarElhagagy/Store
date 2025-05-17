package com.example.demo.service;

import com.example.demo.entities.Customer;
import com.example.demo.entities.Product;
import com.example.demo.entities.Wishlist;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface WishlistService {
    
    // Add product to customer's wishlist
    Wishlist addToWishlist(Customer customer, Product product);
    
    // Find wishlist by ID
    Optional<Wishlist> findWishlistById(Integer id);
    
    // Find all wishlists
    List<Wishlist> getAllWishlists();
    
    // Get customer's wishlist
    List<Wishlist> getCustomerWishlist(Customer customer);
    
    // Get wishlists for a product
    List<Wishlist> getWishlistsByProduct(Product product);
    
    // Check if product is in customer's wishlist
    boolean isProductInWishlist(Customer customer, Product product);
    
    // Get wishlists added on a specific date
    List<Wishlist> getWishlistsByDate(LocalDate date);
    
    // Get wishlists added after a specific date
    List<Wishlist> getWishlistsAddedAfter(LocalDate date);
    
    // Get wishlists added before a specific date
    List<Wishlist> getWishlistsAddedBefore(LocalDate date);
    
    // Get wishlists added within a date range
    List<Wishlist> getWishlistsAddedBetween(LocalDate startDate, LocalDate endDate);
    
    // Count wishlists for a customer
    long countCustomerWishlists(Customer customer);
    
    // Count wishlists for a product
    long countProductWishlists(Product product);
    
    // Get most popular products in wishlists
    Map<Product, Long> getMostPopularWishlistProducts(int limit);
    
    // Get customers who added a specific product to wishlist
    List<Customer> getCustomersWhoAddedProduct(Product product);
    
    // Remove product from customer's wishlist
    void removeFromWishlist(Customer customer, Product product);
    
    // Remove wishlist by ID
    void removeWishlist(Integer wishlistId);
    
    // Remove all items from customer's wishlist
    void clearCustomerWishlist(Customer customer);
}