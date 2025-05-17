package com.example.demo.repositories;

import com.example.demo.entities.Product;
import com.example.demo.entities.Supplier;
import com.example.demo.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    // Find by product name
    List<Product> findByProductNameContaining(String productNameKeyword);
    
    // Find by brand
    List<Product> findByBrand(String brand);
    
    // Find by color
    List<Product> findByColor(String color);
    
    // Find by size
    List<Product> findBySize(String size);
    
    // Find by supplier
    List<Product> findBySupplier(Supplier supplier);
    
    // Find active products
    List<Product> findByIsActiveTrue();
    
    // Find inactive products
    List<Product> findByIsActiveFalse();
    
    // Find products launched after a date
    List<Product> findByLaunchDateAfter(LocalDate date);
    
    // Find products by price range
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    // Find products cheaper than price
    List<Product> findByPriceLessThanEqual(BigDecimal maxPrice);
    
    // Find products more expensive than price
    List<Product> findByPriceGreaterThanEqual(BigDecimal minPrice);
    
    // Find products by category
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c = :category")
    List<Product> findByCategory(@Param("category") Category category);
    
    // Find products with reviews
    @Query("SELECT DISTINCT p FROM Product p WHERE SIZE(p.reviews) > 0")
    List<Product> findProductsWithReviews();
    
    // Find products with average rating above threshold
    @Query("SELECT p, AVG(r.rating) as avgRating FROM Product p JOIN p.reviews r GROUP BY p HAVING AVG(r.rating) >= :minRating")
    List<Object[]> findProductsWithMinimumAverageRating(@Param("minRating") Double minRating);
    
    // Find products in stock at any store
    @Query("SELECT DISTINCT p FROM Product p JOIN p.storeInventories si WHERE si.quantity > 0")
    List<Product> findProductsInStock();
    
    // Find top-selling products
    @Query("SELECT p, SUM(od.quantity) as totalSold FROM Product p JOIN p.orderDetails od GROUP BY p ORDER BY totalSold DESC")
    List<Object[]> findTopSellingProducts();
    
    // Find products by multiple categories
    @Query("SELECT DISTINCT p FROM Product p JOIN p.categories c WHERE c IN :categories")
    List<Product> findByCategories(@Param("categories") List<Category> categories);
    
    // Find products by brand and price range
    List<Product> findByBrandAndPriceBetween(String brand, BigDecimal minPrice, BigDecimal maxPrice);
    
    // Find products by brand and color
    List<Product> findByBrandAndColor(String brand, String color);
    
    // Find latest products by launch date
    List<Product> findTop10ByOrderByLaunchDateDesc();
    
    // Count products by brand
    long countByBrand(String brand);
    
    // Check if product exists by name and brand
    boolean existsByProductNameAndBrand(String productName, String brand);
}