package com.example.demo.service;

import com.example.demo.entities.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ProductService {
    
    // Create a new product
    Product createProduct(String productName, String size, String brand, BigDecimal price, 
                         String color, String description, Supplier supplier);
    
    // Find product by ID
    Optional<Product> findProductById(Integer id);
    
    // Get all products
    List<Product> getAllProducts();
    
    // Get products by name keyword
    List<Product> getProductsByNameKeyword(String productNameKeyword);
    
    // Get products by brand
    List<Product> getProductsByBrand(String brand);
    
    // Get products by color
    List<Product> getProductsByColor(String color);
    
    // Get products by size
    List<Product> getProductsBySize(String size);
    
    // Get products by supplier
    List<Product> getProductsBySupplier(Supplier supplier);
    
    // Get active products
    List<Product> getActiveProducts();
    
    // Get inactive products
    List<Product> getInactiveProducts();
    
    // Get products launched after date
    List<Product> getProductsLaunchedAfter(LocalDate date);
    
    // Get products by price range
    List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    
    // Get products by maximum price
    List<Product> getProductsByMaxPrice(BigDecimal maxPrice);
    
    // Get products by minimum price
    List<Product> getProductsByMinPrice(BigDecimal minPrice);
    
    // Get products by category
    List<Product> getProductsByCategory(Category category);
    
    // Get products by multiple categories
    List<Product> getProductsByCategories(List<Category> categories);
    
    // Get products by brand and price range
    List<Product> getProductsByBrandAndPriceRange(String brand, BigDecimal minPrice, BigDecimal maxPrice);
    
    // Get products by brand and color
    List<Product> getProductsByBrandAndColor(String brand, String color);
    
    // Get latest products
    List<Product> getLatestProducts();
    
    // Get products with reviews
    List<Product> getProductsWithReviews();
    
    // Get products with minimum average rating
    Map<Product, Double> getProductsWithMinimumAverageRating(Double minRating);
    
    // Get products in stock
    List<Product> getProductsInStock();
    
    // Get top selling products
    Map<Product, Long> getTopSellingProducts(int limit);
    
    // Update product details
    Product updateProduct(Product product);
    
    // Update product price
    Product updateProductPrice(Integer productId, BigDecimal newPrice);
    
    // Update product status (active/inactive)
    Product updateProductStatus(Integer productId, Boolean isActive);
    
    // Add product to category
    Product addProductToCategory(Integer productId, Category category);
    
    // Remove product from category
    Product removeProductFromCategory(Integer productId, Category category);
    
    // Get product categories
    Set<Category> getProductCategories(Integer productId);
    
    // Get product images
    Set<Image> getProductImages(Integer productId);
    
    // Get product reviews
    Set<Review> getProductReviews(Integer productId);
    
    // Get product price history
    Set<PriceHistory> getProductPriceHistory(Integer productId);
    
    // Calculate average product rating
    Double calculateAverageProductRating(Integer productId);
    
    // Count products by brand
    long countProductsByBrand(String brand);
    
    // Check if product exists by name and brand
    boolean productExistsByNameAndBrand(String productName, String brand);
    
    // Delete product
    void deleteProduct(Integer productId);
}