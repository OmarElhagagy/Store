package com.example.demo.controller;

import com.example.demo.dto.CartDTO;
import com.example.demo.entities.Cart;
import com.example.demo.service.CartService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for managing shopping carts
 */
@RestController
@RequestMapping("/api/carts")
@Slf4j
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Get a cart by customer ID
     */
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCustomerResourceOwner(#customerId)")
    public ResponseEntity<CartDTO> getCartByCustomer(@PathVariable Integer customerId) {
        log.info("REST request to get Cart for customer ID {}", customerId);
        Optional<Cart> cart = cartService.findByCustomerId(customerId);
        return cart
                .map(value -> ResponseEntity.ok(CartDTO.fromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get cart by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCartResourceOwner(#id)")
    public ResponseEntity<CartDTO> getCart(@PathVariable Integer id) {
        log.info("REST request to get Cart with ID {}", id);
        Optional<Cart> cart = cartService.findById(id);
        return cart
                .map(value -> ResponseEntity.ok(CartDTO.fromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new cart for a customer
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCustomerResourceOwner(#cartDTO.customerId)")
    public ResponseEntity<?> createCart(@Valid @RequestBody CartDTO cartDTO, BindingResult bindingResult) {
        log.info("REST request to create Cart for customer ID {}", cartDTO.getCustomerId());
        
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
        
        try {
            // Check if customer already has a cart
            if (cartService.findByCustomerId(cartDTO.getCustomerId()).isPresent()) {
                return new ResponseEntity<>("Customer already has an active cart", HttpStatus.BAD_REQUEST);
            }
            
            Cart cart = cartService.createCartFromDTO(cartDTO);
            return new ResponseEntity<>(CartDTO.fromEntity(cart), HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error creating cart", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating cart: " + e.getMessage(), e);
        }
    }

    /**
     * Add product to cart
     */
    @PostMapping("/{cartId}/products")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCartResourceOwner(#cartId)")
    public ResponseEntity<CartDTO> addProductToCart(
            @PathVariable Integer cartId,
            @RequestParam Integer productId,
            @RequestParam Integer quantity) {
        log.info("REST request to add product ID {} with quantity {} to Cart ID {}", productId, quantity, cartId);
        try {
            if (quantity <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than zero");
            }
            
            Cart cart = cartService.addProductToCart(cartId, productId, quantity);
            return ResponseEntity.ok(CartDTO.fromEntity(cart));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error adding product to cart", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error adding product to cart: " + e.getMessage(), e);
        }
    }

    /**
     * Update product quantity in cart
     */
    @PutMapping("/{cartId}/products/{productId}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCartResourceOwner(#cartId)")
    public ResponseEntity<CartDTO> updateProductQuantity(
            @PathVariable Integer cartId,
            @PathVariable Integer productId,
            @RequestParam Integer quantity) {
        log.info("REST request to update product ID {} quantity to {} in Cart ID {}", productId, quantity, cartId);
        try {
            if (quantity < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity cannot be negative");
            }
            
            Cart cart;
            if (quantity == 0) {
                // Remove product from cart when quantity is zero
                cart = cartService.removeProductFromCart(cartId, productId);
            } else {
                // Update quantity
                cart = cartService.updateProductQuantity(cartId, productId, quantity);
            }
            
            return ResponseEntity.ok(CartDTO.fromEntity(cart));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating product quantity in cart", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating product quantity: " + e.getMessage(), e);
        }
    }

    /**
     * Remove product from cart
     */
    @DeleteMapping("/{cartId}/products/{productId}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCartResourceOwner(#cartId)")
    public ResponseEntity<CartDTO> removeProductFromCart(
            @PathVariable Integer cartId,
            @PathVariable Integer productId) {
        log.info("REST request to remove product ID {} from Cart ID {}", productId, cartId);
        try {
            Cart cart = cartService.removeProductFromCart(cartId, productId);
            return ResponseEntity.ok(CartDTO.fromEntity(cart));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error removing product from cart", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error removing product from cart: " + e.getMessage(), e);
        }
    }

    /**
     * Clear cart (remove all products)
     */
    @DeleteMapping("/{id}/clear")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCartResourceOwner(#id)")
    public ResponseEntity<CartDTO> clearCart(@PathVariable Integer id) {
        log.info("REST request to clear Cart with ID {}", id);
        try {
            Cart cart = cartService.clearCart(id);
            return ResponseEntity.ok(CartDTO.fromEntity(cart));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error clearing cart", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error clearing cart: " + e.getMessage(), e);
        }
    }

    /**
     * Convert cart to order
     */
    @PostMapping("/{id}/checkout")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCartResourceOwner(#id)")
    public ResponseEntity<?> checkout(@PathVariable Integer id) {
        log.info("REST request to checkout Cart with ID {}", id);
        try {
            Integer orderId = cartService.checkout(id);
            Map<String, Integer> response = new HashMap<>();
            response.put("orderId", orderId);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error checking out cart", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error checking out cart: " + e.getMessage(), e);
        }
    }

    /**
     * Handle validation errors and return appropriate response
     */
    private ResponseEntity<Map<String, String>> handleValidationErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
} 