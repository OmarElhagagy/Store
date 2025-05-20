package com.example.demo.controller;

import com.example.demo.dto.WishlistDTO;
import com.example.demo.entities.Wishlist;
import com.example.demo.service.WishlistService;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Controller for managing customer wishlists
 */
@RestController
@RequestMapping("/api/wishlists")
@Slf4j
public class WishlistController {

    private final WishlistService wishlistService;

    @Autowired
    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    /**
     * Get all wishlists for a customer
     */
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCustomerResourceOwner(#customerId)")
    public ResponseEntity<List<WishlistDTO>> getWishlistsByCustomer(@PathVariable Integer customerId) {
        log.info("REST request to get Wishlists for customer ID {}", customerId);
        List<Wishlist> wishlists = wishlistService.findByCustomerId(customerId);
        List<WishlistDTO> wishlistDTOs = wishlists.stream()
                .map(WishlistDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(wishlistDTOs);
    }

    /**
     * Get a specific wishlist by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isWishlistResourceOwner(#id)")
    public ResponseEntity<WishlistDTO> getWishlist(@PathVariable Integer id) {
        log.info("REST request to get Wishlist with ID {}", id);
        Optional<Wishlist> wishlist = wishlistService.findById(id);
        return wishlist
                .map(value -> ResponseEntity.ok(WishlistDTO.fromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new wishlist
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCustomerResourceOwner(#wishlistDTO.customerId)")
    public ResponseEntity<?> createWishlist(@Valid @RequestBody WishlistDTO wishlistDTO, BindingResult bindingResult) {
        log.info("REST request to create Wishlist for customer ID {}", wishlistDTO.getCustomerId());
        
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
        
        try {
            Wishlist wishlist = new Wishlist();
            wishlistDTO.updateEntity(wishlist);
            
            Wishlist savedWishlist = wishlistService.createWishlist(wishlist);
            
            return new ResponseEntity<>(WishlistDTO.fromEntity(savedWishlist), HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error creating wishlist", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating wishlist: " + e.getMessage(), e);
        }
    }

    /**
     * Update wishlist name and description
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isWishlistResourceOwner(#id)")
    public ResponseEntity<?> updateWishlist(
            @PathVariable Integer id,
            @Valid @RequestBody WishlistDTO wishlistDTO,
            BindingResult bindingResult) {
        log.info("REST request to update Wishlist with ID {}", id);
        
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
        
        try {
            Wishlist existingWishlist = wishlistService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Wishlist not found with ID " + id));
            
            // Update only name and description, don't change products
            existingWishlist.setName(wishlistDTO.getName());
            existingWishlist.setDescription(wishlistDTO.getDescription());
            
            Wishlist updatedWishlist = wishlistService.updateWishlist(existingWishlist);
            
            return ResponseEntity.ok(WishlistDTO.fromEntity(updatedWishlist));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error updating wishlist", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating wishlist: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a wishlist
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isWishlistResourceOwner(#id)")
    public ResponseEntity<Void> deleteWishlist(@PathVariable Integer id) {
        log.info("REST request to delete Wishlist with ID {}", id);
        try {
            wishlistService.deleteWishlist(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting wishlist", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting wishlist: " + e.getMessage(), e);
        }
    }

    /**
     * Add a product to wishlist
     */
    @PostMapping("/{id}/products")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isWishlistResourceOwner(#id)")
    public ResponseEntity<WishlistDTO> addProductToWishlist(
            @PathVariable Integer id,
            @RequestParam Integer productId) {
        log.info("REST request to add product ID {} to Wishlist ID {}", productId, id);
        try {
            Wishlist wishlist = wishlistService.addProductToWishlist(id, productId);
            return ResponseEntity.ok(WishlistDTO.fromEntity(wishlist));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error adding product to wishlist", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error adding product to wishlist: " + e.getMessage(), e);
        }
    }

    /**
     * Remove a product from wishlist
     */
    @DeleteMapping("/{id}/products/{productId}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isWishlistResourceOwner(#id)")
    public ResponseEntity<WishlistDTO> removeProductFromWishlist(
            @PathVariable Integer id,
            @PathVariable Integer productId) {
        log.info("REST request to remove product ID {} from Wishlist ID {}", productId, id);
        try {
            Wishlist wishlist = wishlistService.removeProductFromWishlist(id, productId);
            return ResponseEntity.ok(WishlistDTO.fromEntity(wishlist));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error removing product from wishlist", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error removing product from wishlist: " + e.getMessage(), e);
        }
    }

    /**
     * Move a product from wishlist to cart
     */
    @PostMapping("/{id}/products/{productId}/move-to-cart")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isWishlistResourceOwner(#id)")
    public ResponseEntity<Map<String, Object>> moveProductToCart(
            @PathVariable Integer id,
            @PathVariable Integer productId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        log.info("REST request to move product ID {} from Wishlist ID {} to cart with quantity {}", 
                productId, id, quantity);
        try {
            if (quantity <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than zero");
            }
            
            Map<String, Object> result = wishlistService.moveProductToCart(id, productId, quantity);
            return ResponseEntity.ok(result);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error moving product to cart", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error moving product to cart: " + e.getMessage(), e);
        }
    }

    /**
     * Share a wishlist (make it public or generate a share link)
     */
    @PutMapping("/{id}/share")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isWishlistResourceOwner(#id)")
    public ResponseEntity<Map<String, String>> shareWishlist(@PathVariable Integer id) {
        log.info("REST request to share Wishlist with ID {}", id);
        try {
            String shareUrl = wishlistService.shareWishlist(id);
            Map<String, String> response = new HashMap<>();
            response.put("shareUrl", shareUrl);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error sharing wishlist", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error sharing wishlist: " + e.getMessage(), e);
        }
    }

    /**
     * Make a wishlist private (remove sharing)
     */
    @PutMapping("/{id}/unshare")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isWishlistResourceOwner(#id)")
    public ResponseEntity<WishlistDTO> unshareWishlist(@PathVariable Integer id) {
        log.info("REST request to unshare Wishlist with ID {}", id);
        try {
            Wishlist wishlist = wishlistService.unshareWishlist(id);
            return ResponseEntity.ok(WishlistDTO.fromEntity(wishlist));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error unsharing wishlist", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error unsharing wishlist: " + e.getMessage(), e);
        }
    }

    /**
     * Get a shared wishlist by share code (public access)
     */
    @GetMapping("/shared/{shareCode}")
    public ResponseEntity<WishlistDTO> getSharedWishlist(@PathVariable String shareCode) {
        log.info("REST request to get shared Wishlist with code {}", shareCode);
        try {
            Optional<Wishlist> wishlist = wishlistService.findByShareCode(shareCode);
            return wishlist
                    .map(value -> ResponseEntity.ok(WishlistDTO.fromEntity(value)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error retrieving shared wishlist", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving shared wishlist: " + e.getMessage(), e);
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