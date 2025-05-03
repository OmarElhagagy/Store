package com.example.demo.service.impl;

import com.example.demo.entities.Cart;
import com.example.demo.entities.Customer;
import com.example.demo.entities.Product;
import com.example.demo.repositories.CartRepository;
import com.example.demo.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    @Override
    public Optional<Cart> getCartById(Integer id) {
        return cartRepository.findById(id);
    }

    @Override
    public List<Cart> getCartsByCustomer(Customer customer) {
        return cartRepository.findByCustomer(customer);
    }

    @Override
    public List<Cart> getCartsByProduct(Product product) {
        return cartRepository.findByProduct(product);
    }

    @Override
    public Optional<Cart> getCartByCustomerAndProduct(Customer customer, Product product) {
        return cartRepository.findByCustomerAndproduct(customer, product);
    }

    @Override
    @Transactional
    public Cart saveCart(Cart cart) {
        if(cart.getAddedDate() == null) {
            cart.setAddedDate(LocalDate.now());
        }
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void deleteCart(Integer id) {
        cartRepository.deleteById(id);
    }

    @Override
    @Transactional
    public boolean existsById(Integer id) {
        return cartRepository.existsById(id);
    }

    @Override
    @Transactional
    public void updateCartQuantity(Integer cartId, Integer quantity){
        cartRepository.findById(cartId).ifPresent(cart -> {
            cart.setQuantity(quantity);
            cartRepository.save(cart);
        });
    }
}
