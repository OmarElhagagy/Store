package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.entities.User;
import com.example.demo.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
 * REST Controller for managing system users (admin, staff)
 */
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get all users with pagination
     * Restricted to admin users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable) {
        log.info("REST request to get all Users with pagination");
        Page<User> users = userService.findAllWithPagination(pageable);
        Page<UserDTO> userDTOs = users.map(UserDTO::fromEntity);
        return ResponseEntity.ok(userDTOs);
    }

    /**
     * Get users by role
     * Restricted to admin users
     */
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDTO>> getUsersByRole(@PathVariable String role, Pageable pageable) {
        log.info("REST request to get Users with role {}", role);
        Page<User> users = userService.findByRole(role, pageable);
        Page<UserDTO> userDTOs = users.map(UserDTO::fromEntity);
        return ResponseEntity.ok(userDTOs);
    }

    /**
     * Get a specific user by ID
     * Restricted to admin users or the user themselves
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isUserResourceOwner(#id)")
    public ResponseEntity<UserDTO> getUser(@PathVariable Integer id) {
        log.info("REST request to get User with ID {}", id);
        Optional<User> user = userService.findById(id);
        return user
                .map(value -> ResponseEntity.ok(UserDTO.fromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get current logged in user
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        log.info("REST request to get current User");
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(UserDTO.fromEntity(currentUser));
    }

    /**
     * Create a new user
     * Restricted to admin users
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {
        log.info("REST request to create a new User with username {}", userDTO.getUsername());
        
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
        
        try {
            // Check if username is already in use
            if (userService.findByUsername(userDTO.getUsername()).isPresent()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("username", "Username is already in use");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
            
            // Check if email is already in use
            if (userService.findByEmail(userDTO.getEmail()).isPresent()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("email", "Email is already in use");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
            
            User user = new User();
            userDTO.updateEntity(user);
            
            User savedUser = userService.createUser(user);
            
            return new ResponseEntity<>(UserDTO.fromEntity(savedUser), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating user", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating user: " + e.getMessage(), e);
        }
    }

    /**
     * Update an existing user
     * Restricted to admin users or the user themselves
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isUserResourceOwner(#id)")
    public ResponseEntity<?> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UserDTO userDTO,
            BindingResult bindingResult) {
        log.info("REST request to update User with ID {}", id);
        
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
        
        try {
            // Find existing user
            User existingUser = userService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID " + id));
            
            // Check if username is being changed to one that already exists
            if (!existingUser.getUsername().equals(userDTO.getUsername()) && 
                    userService.findByUsername(userDTO.getUsername()).isPresent()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("username", "Username is already in use");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
            
            // Check if email is being changed to one that already exists
            if (!existingUser.getEmail().equals(userDTO.getEmail()) && 
                    userService.findByEmail(userDTO.getEmail()).isPresent()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("email", "Email is already in use");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
            
            // Only allow admins to change roles
            if (!existingUser.getRole().equals(userDTO.getRole()) && 
                    !userService.currentUserHasRole("ADMIN")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only administrators can change user roles");
            }
            
            userDTO.updateEntity(existingUser);
            
            User updatedUser = userService.updateUser(existingUser);
            
            return ResponseEntity.ok(UserDTO.fromEntity(updatedUser));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating user", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating user: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a user
     * Restricted to admin users
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        log.info("REST request to delete User with ID {}", id);
        try {
            // Prevent self-deletion
            User currentUser = userService.getCurrentUser();
            if (currentUser.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete your own user account");
            }
            
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting user", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting user: " + e.getMessage(), e);
        }
    }

    /**
     * Change user's password
     * Restricted to admin users or the user themselves
     */
    @PutMapping("/{id}/change-password")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isUserResourceOwner(#id)")
    public ResponseEntity<Void> changePassword(
            @PathVariable Integer id,
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {
        log.info("REST request to change password for User ID {}", id);
        try {
            userService.changePassword(id, currentPassword, newPassword);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error changing password", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error changing password: " + e.getMessage(), e);
        }
    }

    /**
     * Reset a user's password (admin only)
     * Generates a temporary password
     */
    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> resetPassword(@PathVariable Integer id) {
        log.info("REST request to reset password for User ID {}", id);
        try {
            String temporaryPassword = userService.resetPassword(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Password has been reset");
            response.put("temporaryPassword", temporaryPassword);
            
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error resetting password", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error resetting password: " + e.getMessage(), e);
        }
    }
    
    /**
     * Lock a user account
     * Restricted to admin users
     */
    @PutMapping("/{id}/lock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> lockUserAccount(@PathVariable Integer id) {
        log.info("REST request to lock User account with ID {}", id);
        try {
            // Prevent locking your own account
            User currentUser = userService.getCurrentUser();
            if (currentUser.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot lock your own user account");
            }
            
            User user = userService.lockUser(id);
            return ResponseEntity.ok(UserDTO.fromEntity(user));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error locking user account", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error locking user account: " + e.getMessage(), e);
        }
    }

    /**
     * Unlock a user account
     * Restricted to admin users
     */
    @PutMapping("/{id}/unlock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> unlockUserAccount(@PathVariable Integer id) {
        log.info("REST request to unlock User account with ID {}", id);
        try {
            User user = userService.unlockUser(id);
            return ResponseEntity.ok(UserDTO.fromEntity(user));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error unlocking user account", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error unlocking user account: " + e.getMessage(), e);
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