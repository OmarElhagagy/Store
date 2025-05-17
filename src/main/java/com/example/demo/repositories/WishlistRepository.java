package com.example.demo.repositories;

import com.example.demo.entities.Customer;
import com.example.demo.entities.Product;
import com.example.demo.entities.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    
    // Find all wishlists for a customer
    List<Wishlist> findByCustomer(Customer customer);
    
    // Find all wishlists containing a specific product
    List<Wishlist> findByProduct(Product product);
    
    // Find specific wishlist item by customer and product
    Optional<Wishlist> findByCustomerAndProduct(Customer customer, Product product);
    
    // Find wishlists added on a specific date
    List<Wishlist> findByAddedDate(LocalDate addedDate);
    
    // Find wishlists added after a specific date
    List<Wishlist> findByAddedDateAfter(LocalDate date);
    
    // Find wishlists added before a specific date
    List<Wishlist> findByAddedDateBefore(LocalDate date);
    
    // Find wishlists added between two dates
    List<Wishlist> findByAddedDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Count total wishlists for a customer
    long countByCustomer(Customer customer);
    
    // Count total wishlists for a product
    long countByProduct(Product product);
    
    // Check if a product exists in a customer's wishlist
    boolean existsByCustomerAndProduct(Customer customer, Product product);
    
    // Find most popular products in wishlists
    @Query("SELECT w.product, COUNT(w) as count FROM Wishlist w GROUP BY w.product ORDER BY count DESC")
    List<Object[]> findMostPopularProducts();
    
    // Find customers who added a specific product to wishlist
    @Query("SELECT w.customer FROM Wishlist w WHERE w.product = :product")
    List<Customer> findCustomersWhoAddedProduct(Product product);
}