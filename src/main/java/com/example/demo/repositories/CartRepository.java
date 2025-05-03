package com.example.demo.repositories;

import com.example.demo.entities.Cart;
import com.example.demo.entities.Customer;
import com.example.demo.entities.Product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findByCustomer(Customer customer);
    List<Cart> findByProduct(Product product);
    Optional<Cart> findByCustomerAndproduct(Customer customer, Product product);
}
