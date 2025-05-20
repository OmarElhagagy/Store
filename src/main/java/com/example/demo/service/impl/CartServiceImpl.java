package com.example.demo.service.impl;

import com.example.demo.dto.CartDTO;
import com.example.demo.entities.Cart;
import com.example.demo.entities.Customer;
import com.example.demo.entities.CustomerOrder;
import com.example.demo.entities.Product;
import com.example.demo.repositories.CartRepository;
import com.example.demo.repositories.CustomerOrderRepository;
import com.example.demo.repositories.CustomerRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.service.CartService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CustomerOrderRepository orderRepository;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository,
                          CustomerRepository customerRepository,
                          ProductRepository productRepository,
                          CustomerOrderRepository orderRepository) {
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
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

    @Override
    public Optional<Cart> findById(Integer id) {
        return getCartById(id);
    }
    
    @Override
    public Optional<Cart> findByCustomerId(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + customerId));
        List<Cart> carts = getCartsByCustomer(customer);
        return carts.isEmpty() ? Optional.empty() : Optional.of(carts.get(0));
    }
    
    @Override
    @Transactional
    public Cart createCartFromDTO(CartDTO cartDTO) {
        Cart cart = new Cart();
        
        Customer customer = customerRepository.findById(cartDTO.getCustomerId())
            .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + cartDTO.getCustomerId()));
        cart.setCustomer(customer);
        
        Product product = productRepository.findById(cartDTO.getProductId())
            .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + cartDTO.getProductId()));
        cart.setProduct(product);
        
        cart.setQuantity(cartDTO.getQuantity());
        cart.setAddedDate(LocalDate.now());
        
        return saveCart(cart);
    }
    
    @Override
    @Transactional
    public Cart addProductToCart(Integer customerId, Integer productId, Integer quantity) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + customerId));
            
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));
            
        // Check if product already in cart
        Optional<Cart> existingCart = getCartByCustomerAndProduct(customer, product);
        
        if (existingCart.isPresent()) {
            // Update quantity if product already in cart
            Cart cart = existingCart.get();
            cart.setQuantity(cart.getQuantity() + quantity);
            return saveCart(cart);
        } else {
            // Add new product to cart
            Cart newCart = new Cart();
            newCart.setCustomer(customer);
            newCart.setProduct(product);
            newCart.setQuantity(quantity);
            newCart.setAddedDate(LocalDate.now());
            return saveCart(newCart);
        }
    }
    
    @Override
    @Transactional
    public Cart removeProductFromCart(Integer cartId, Integer productId) {
        Cart cart = findById(cartId)
            .orElseThrow(() -> new EntityNotFoundException("Cart not found with ID: " + cartId));
            
        if (!cart.getProduct().getId().equals(productId)) {
            throw new IllegalArgumentException("Product ID does not match the product in cart");
        }
        
        // Delete the cart item
        deleteCart(cart.getId());
        
        return cart; // Return the deleted cart for reference
    }
    
    @Override
    @Transactional
    public Cart updateProductQuantity(Integer cartId, Integer productId, Integer quantity) {
        Cart cart = findById(cartId)
            .orElseThrow(() -> new EntityNotFoundException("Cart not found with ID: " + cartId));
            
        if (!cart.getProduct().getId().equals(productId)) {
            throw new IllegalArgumentException("Product ID does not match the product in cart");
        }
        
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        
        cart.setQuantity(quantity);
        return saveCart(cart);
    }
    
    @Override
    @Transactional
    public Cart clearCart(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + customerId));
            
        List<Cart> carts = getCartsByCustomer(customer);
        if (carts.isEmpty()) {
            throw new EntityNotFoundException("No cart found for customer ID: " + customerId);
        }
        
        cartRepository.deleteAll(carts);
        
        // Return an empty cart reference for the customer
        Cart emptyCart = new Cart();
        emptyCart.setCustomer(customer);
        emptyCart.setQuantity(0);
        return emptyCart;
    }
    
    @Override
    @Transactional
    public Integer checkout(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + customerId));
            
        List<Cart> cartItems = getCartsByCustomer(customer);
        
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }
        
        // Create a new order
        CustomerOrder order = new CustomerOrder();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        
        // Calculate total amount
        double totalAmount = cartItems.stream()
            .mapToDouble(cart -> cart.getProduct().getPrice().doubleValue() * cart.getQuantity())
            .sum();
            
        order.setTotalAmount(totalAmount);
        
        // Save the order
        CustomerOrder savedOrder = orderRepository.save(order);
        
        // Clear the cart after checkout
        clearCart(customerId);
        
        return savedOrder.getId();
    }
}
