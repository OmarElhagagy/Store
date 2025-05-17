package com.example.demo.service.impl;

import com.example.demo.entities.*;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;


@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Product createProduct(String productName, String size, String brand, BigDecimal price, 
                               String color, String description, Supplier supplier) {
        Product product = new Product();
        product.setProductName(productName);
        product.setSize(size);
        product.setBrand(brand);
        product.setPrice(price);
        product.setColor(color);
        product.setDescription(description);
        product.setSupplier(supplier);
        product.setLaunchDate(LocalDate.now());
        product.setIsActive(true);
        
        // Initialize sets
        product.setCategories(new HashSet<>());
        product.setImages(new HashSet<>());
        product.setCarts(new HashSet<>());
        product.setNotifications(new HashSet<>());
        product.setOrderDetails(new HashSet<>());
        product.setPriceHistories(new HashSet<>());
        product.setReservedStocks(new HashSet<>());
        product.setReturnFields(new HashSet<>());
        product.setReviews(new HashSet<>());
        product.setStoreInventories(new HashSet<>());
        product.setSupplyOrders(new HashSet<>());
        product.setWishlists(new HashSet<>());
        
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> findProductById(Integer id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsByNameKeyword(String productNameKeyword) {
        return productRepository.findByProductNameContaining(productNameKeyword);
    }

    @Override
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    @Override
    public List<Product> getProductsByColor(String color) {
        return productRepository.findByColor(color);
    }

    @Override
    public List<Product> getProductsBySize(String size) {
        return productRepository.findBySize(size);
    }

    @Override
    public List<Product> getProductsBySupplier(Supplier supplier) {
        return productRepository.findBySupplier(supplier);
    }

    @Override
    public List<Product> getActiveProducts() {
        return productRepository.findByIsActiveTrue();
    }

    @Override
    public List<Product> getInactiveProducts() {
        return productRepository.findByIsActiveFalse();
    }

    @Override
    public List<Product> getProductsLaunchedAfter(LocalDate date) {
        return productRepository.findByLaunchDateAfter(date);
    }

    @Override
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    @Override
    public List<Product> getProductsByMaxPrice(BigDecimal maxPrice) {
        return productRepository.findByPriceLessThanEqual(maxPrice);
    }

    @Override
    public List<Product> getProductsByMinPrice(BigDecimal minPrice) {
        return productRepository.findByPriceGreaterThanEqual(minPrice);
    }

    @Override
    public List<Product> getProductsByCategory(Category category) {
        return productRepository.findByCategory(category);
    }
    
    @Override
    public List<Product> getProductsByCategories(List<Category> categories) {
        return productRepository.findByCategories(categories);
    }
    
    @Override
    public List<Product> getProductsByBrandAndPriceRange(String brand, BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByBrandAndPriceBetween(brand, minPrice, maxPrice);
    }
    
    @Override
    public List<Product> getProductsByBrandAndColor(String brand, String color) {
        return productRepository.findByBrandAndColor(brand, color);
    }
    
    @Override
    public List<Product> getLatestProducts() {
        return productRepository.findTop10ByOrderByLaunchDateDesc();
    }

    @Override
    public List<Product> getProductsWithReviews() {
        return productRepository.findProductsWithReviews();
    }

    @Override
    public Map<Product, Double> getProductsWithMinimumAverageRating(Double minRating) {
        List<Object[]> results = productRepository.findProductsWithMinimumAverageRating(minRating);
        
        Map<Product, Double> ratedProducts = new LinkedHashMap<>();
        for (Object[] result : results) {
            Product product = (Product) result[0];
            Double rating = (Double) result[1];
            ratedProducts.put(product, rating);
        }
        
        return ratedProducts;
    }

    @Override
    public List<Product> getProductsInStock() {
        return productRepository.findProductsInStock();
    }

    @Override
    public Map<Product, Long> getTopSellingProducts(int limit) {
        List<Object[]> results = productRepository.findTopSellingProducts();
        
        Map<Product, Long> topProducts = new LinkedHashMap<>();
        int count = 0;
        
        for (Object[] result : results) {
            if (count >= limit) break;
            
            Product product = (Product) result[0];
            Long salesCount = (Long) result[1];
            topProducts.put(product, salesCount);
            count++;
        }
        
        return topProducts;
    }

    @Override
    @Transactional
    public Product updateProduct(Product product) {
        // Make sure the product exists
        if (!productRepository.existsById(product.getId())) {
            throw new IllegalArgumentException("Product not found with ID: " + product.getId());
        }
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProductPrice(Integer productId, BigDecimal newPrice) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            // Simply update the price
            product.setPrice(newPrice);
            return productRepository.save(product);
        }
        
        throw new IllegalArgumentException("Product not found with ID: " + productId);
    }

    @Override
    @Transactional
    public Product updateProductStatus(Integer productId, Boolean isActive) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setIsActive(isActive);
            return productRepository.save(product);
        }
        
        throw new IllegalArgumentException("Product not found with ID: " + productId);
    }

    @Override
    @Transactional
    public Product addProductToCategory(Integer productId, Category category) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            if (category != null) {
                product.getCategories().add(category);
                return productRepository.save(product);
            } else {
                throw new IllegalArgumentException("Category cannot be null");
            }
        }
        
        throw new IllegalArgumentException("Product not found with ID: " + productId);
    }

    @Override
    @Transactional
    public Product removeProductFromCategory(Integer productId, Category category) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            if (category != null) {
                product.getCategories().remove(category);
                return productRepository.save(product);
            } else {
                throw new IllegalArgumentException("Category cannot be null");
            }
        }
        
        throw new IllegalArgumentException("Product not found with ID: " + productId);
    }

    @Override
    public Set<Category> getProductCategories(Integer productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        
        if (optionalProduct.isPresent()) {
            return optionalProduct.get().getCategories();
        }
        
        throw new IllegalArgumentException("Product not found with ID: " + productId);
    }

    @Override
    public Set<Image> getProductImages(Integer productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        
        if (optionalProduct.isPresent()) {
            return optionalProduct.get().getImages();
        }
        
        throw new IllegalArgumentException("Product not found with ID: " + productId);
    }

    @Override
    public Set<Review> getProductReviews(Integer productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        
        if (optionalProduct.isPresent()) {
            return optionalProduct.get().getReviews();
        }
        
        throw new IllegalArgumentException("Product not found with ID: " + productId);
    }

    @Override
    public Set<PriceHistory> getProductPriceHistory(Integer productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        
        if (optionalProduct.isPresent()) {
            return optionalProduct.get().getPriceHistories();
        }
        
        throw new IllegalArgumentException("Product not found with ID: " + productId);
    }

    @Override
    public Double calculateAverageProductRating(Integer productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            Set<Review> reviews = product.getReviews();
            
            if (reviews == null || reviews.isEmpty()) {
                return 0.0;
            }
            
            double totalRating = 0.0;
            for (Review review : reviews) {
                if (review.getRating() != null) {
                    totalRating += review.getRating();
                }
            }
            
            return totalRating / reviews.size();
        }
        
        throw new IllegalArgumentException("Product not found with ID: " + productId);
    }

    @Override
    public long countProductsByBrand(String brand) {
        if (brand == null || brand.trim().isEmpty()) {
            throw new IllegalArgumentException("Brand cannot be null or empty");
        }
        return productRepository.countByBrand(brand);
    }

    @Override
    public boolean productExistsByNameAndBrand(String productName, String brand) {
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (brand == null || brand.trim().isEmpty()) {
            throw new IllegalArgumentException("Brand cannot be null or empty");
        }
        return productRepository.existsByProductNameAndBrand(productName, brand);
    }

    @Override
    @Transactional
    public void deleteProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Product not found with ID: " + productId);
        }
        productRepository.deleteById(productId);
    }
}