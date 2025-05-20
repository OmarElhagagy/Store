package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.entities.User;
<<<<<<< HEAD
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Controller", description = "API to manage user accounts")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

=======
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

>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

<<<<<<< HEAD
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Returns a list of all users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        logger.info("Fetching all users");
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOs = users.stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userAuthorizationService.isSameUser(authentication, #id)")
    @Operation(summary = "Get user by ID", description = "Returns a single user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserDTO> getUserById(
            @Parameter(description = "User ID", required = true) @PathVariable Integer id) {
        logger.info("Fetching user with ID: {}", id);
        
        User user = userService.findUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        return ResponseEntity.ok(UserDTO.fromEntity(user));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Returns the currently authenticated user")
    public ResponseEntity<UserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        logger.info("Fetching current user: {}", currentUsername);
        
        User user = userService.findByEmail(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + currentUsername));
        
        return ResponseEntity.ok(UserDTO.fromEntity(user));
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Creates a new user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserDTO userDTO) {
        logger.info("Registering new user with email: {}", userDTO.getEmail());
        
        // Check if email already exists
        if (userService.existsByEmail(userDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        User newUser = userService.createUser(
                userDTO.getEmail(),
                userDTO.getPassword(),
                userDTO.getFirstName(),
                userDTO.getLastName(),
                "ROLE_USER" // Default role for new registrations
        );
        
        UserDTO createdUserDTO = UserDTO.fromEntity(newUser);
        // Don't return password hash
        createdUserDTO.setPassword(null);
        
        return new ResponseEntity<>(createdUserDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userAuthorizationService.isSameUser(authentication, #id)")
    @Operation(summary = "Update user", description = "Updates an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "User ID", required = true) @PathVariable Integer id,
            @Valid @RequestBody UserDTO userDTO) {
        logger.info("Updating user with ID: {}", id);
        
        User existingUser = userService.findUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        // Check if email already exists (excluding the current user)
        if (!existingUser.getEmail().equals(userDTO.getEmail()) && 
                userService.existsByEmail(userDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        
        // Only update password if provided
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser = userService.updatePassword(existingUser, userDTO.getPassword());
        } else {
            existingUser = userService.updateUser(existingUser);
        }
        
        UserDTO updatedUserDTO = UserDTO.fromEntity(existingUser);
        // Don't return password hash
        updatedUserDTO.setPassword(null);
        
        return ResponseEntity.ok(updatedUserDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userAuthorizationService.isSameUser(authentication, #id)")
    @Operation(summary = "Delete user", description = "Deletes a user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID", required = true) @PathVariable Integer id) {
        logger.info("Deleting user with ID: {}", id);
        
        if (!userService.findUserById(id).isPresent()) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user role", description = "Updates a user's role")
    public ResponseEntity<UserDTO> updateUserRole(
            @PathVariable Integer id, 
            @RequestBody Map<String, String> roleRequest) {
        String role = roleRequest.get("role");
        logger.info("Updating role for user ID: {} to {}", id, role);
        
        if (role == null || role.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        User user = userService.findUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        user = userService.updateUserRole(user, role);
        
        UserDTO updatedUserDTO = UserDTO.fromEntity(user);
        // Don't return password hash
        updatedUserDTO.setPassword(null);
        
        return ResponseEntity.ok(updatedUserDTO);
    }
    
    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Changes the password for the current user")
    public ResponseEntity<Void> changePassword(@RequestBody Map<String, String> passwordRequest) {
        String currentPassword = passwordRequest.get("currentPassword");
        String newPassword = passwordRequest.get("newPassword");
        
        if (currentPassword == null || newPassword == null || 
                currentPassword.isEmpty() || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        
        boolean success = userService.changePassword(currentUsername, currentPassword, newPassword);
        
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
=======
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
>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044
} 