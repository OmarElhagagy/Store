package com.example.demo.repositories;

import com.example.demo.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {

    // Basic queries
    Optional<Product> findByProductName(String productName);
    List<Product> findByBrand(String brand);
    List<Product> findByIsActiveTrue();
    
    // Price range queries
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    List<Product> findByPriceLessThan(BigDecimal price);
    List<Product> findByPriceGreaterThan(BigDecimal price);
    
    // Search queries
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.productName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> searchProducts(@Param("query") String query);
    
    // Category queries
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.id = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") Integer categoryId);
    
    // Supplier queries
    List<Product> findBySupplierId(Integer supplierId);
    
    // Inventory status
    @Query("SELECT p FROM Product p WHERE " +
           "(SELECT COALESCE(SUM(si.quantity), 0) FROM StoreInventory si WHERE si.product = p) > 0")
    List<Product> findInStockProducts();
    
    // Popular products
    @Query("SELECT p, COUNT(od) as orderCount FROM Product p LEFT JOIN p.orderDetails od " +
           "GROUP BY p ORDER BY orderCount DESC LIMIT :limit")
    List<Object[]> findTopSellingProducts(@Param("limit") int limit);
}