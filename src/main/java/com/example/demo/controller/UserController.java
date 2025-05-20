package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.entities.User;
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

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

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
} 