package com.example.demo.service.impl;

import com.example.demo.entities.Customer;
import com.example.demo.entities.Product;
import com.example.demo.entities.Wishlist;
import com.example.demo.repositories.WishlistRepository;
import com.example.demo.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;


@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;

    @Autowired
    public WishlistServiceImpl(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    @Override
    @Transactional
    public Wishlist addToWishlist(Customer customer, Product product) {
        // Check if already in wishlist
        if (wishlistRepository.existsByCustomerAndProduct(customer, product)) {
            throw new IllegalStateException("Product already in wishlist for this customer");
        }
        
        Wishlist wishlist = new Wishlist();
        wishlist.setCustomer(customer);
        wishlist.setProduct(product);
        wishlist.setAddedDate(LocalDate.now());
        
        return wishlistRepository.save(wishlist);
    }

    @Override
    public Optional<Wishlist> findWishlistById(Integer id) {
        return wishlistRepository.findById(id);
    }

    @Override
    public List<Wishlist> getAllWishlists() {
        return wishlistRepository.findAll();
    }

    @Override
    public List<Wishlist> getCustomerWishlist(Customer customer) {
        return wishlistRepository.findByCustomer(customer);
    }

    @Override
    public List<Wishlist> getWishlistsByProduct(Product product) {
        return wishlistRepository.findByProduct(product);
    }

    @Override
    public boolean isProductInWishlist(Customer customer, Product product) {
        return wishlistRepository.existsByCustomerAndProduct(customer, product);
    }

    @Override
    public List<Wishlist> getWishlistsByDate(LocalDate date) {
        return wishlistRepository.findByAddedDate(date);
    }

    @Override
    public List<Wishlist> getWishlistsAddedAfter(LocalDate date) {
        return wishlistRepository.findByAddedDateAfter(date);
    }

    @Override
    public List<Wishlist> getWishlistsAddedBefore(LocalDate date) {
        return wishlistRepository.findByAddedDateBefore(date);
    }

    @Override
    public List<Wishlist> getWishlistsAddedBetween(LocalDate startDate, LocalDate endDate) {
        return wishlistRepository.findByAddedDateBetween(startDate, endDate);
    }

    @Override
    public long countCustomerWishlists(Customer customer) {
        return wishlistRepository.countByCustomer(customer);
    }

    @Override
    public long countProductWishlists(Product product) {
        return wishlistRepository.countByProduct(product);
    }

    @Override
    public Map<Product, Long> getMostPopularWishlistProducts(int limit) {
        List<Object[]> results = wishlistRepository.findMostPopularProducts();
        
        Map<Product, Long> popularProducts = new LinkedHashMap<>();
        int count = 0;
        
        for (Object[] result : results) {
            if (count >= limit) break;
            
            Product product = (Product) result[0];
            Long popularity = (Long) result[1];
            popularProducts.put(product, popularity);
            count++;
        }
        
        return popularProducts;
    }

    @Override
    public List<Customer> getCustomersWhoAddedProduct(Product product) {
        return wishlistRepository.findCustomersWhoAddedProduct(product);
    }

    @Override
    @Transactional
    public void removeFromWishlist(Customer customer, Product product) {
        Optional<Wishlist> wishlistItem = wishlistRepository.findByCustomerAndProduct(customer, product);
        
        if (wishlistItem.isPresent()) {
            wishlistRepository.delete(wishlistItem.get());
        } else {
            throw new IllegalArgumentException("Product not found in customer's wishlist");
        }
    }

    @Override
    @Transactional
    public void removeWishlist(Integer wishlistId) {
        wishlistRepository.deleteById(wishlistId);
    }

    @Override
    @Transactional
    public void clearCustomerWishlist(Customer customer) {
        List<Wishlist> customerWishlist = wishlistRepository.findByCustomer(customer);
        wishlistRepository.deleteAll(customerWishlist);
    }
}