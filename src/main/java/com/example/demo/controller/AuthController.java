package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entities.Customer;
import com.example.demo.entities.RefreshToken;
import com.example.demo.entities.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.TokenRefreshException;
import com.example.demo.repositories.CustomerRepository;
import com.example.demo.security.jwt.JwtUtils;
import com.example.demo.security.services.UserDetailsImpl;
import com.example.demo.service.EmailService;
import com.example.demo.service.PasswordResetService;
import com.example.demo.service.RefreshTokenService;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication API endpoints")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticates a user with email and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful login", 
                content = @Content(schema = @Schema(implementation = JwtResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials or locked account")
    })
    @SecurityRequirements()
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            String jwt = jwtUtils.generateJwtToken(authentication);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
            
            // Reset failed login attempts
            userService.resetFailedLoginAttempts(userDetails.getId());

            return ResponseEntity.ok(new JwtResponse(
                    jwt,
                    refreshToken.getToken(),
                    userDetails.getId(),
                    userDetails.getEmail(),
                    userDetails.getAuthorities().stream().findFirst().get().getAuthority().replace("ROLE_", "")));
        } catch (BadCredentialsException e) {
            // Increment failed login attempts if user exists
            userService.findUserByEmail(loginRequest.getEmail())
                    .ifPresent(user -> userService.recordFailedLoginAttempt(user.getId()));
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new HashMap<String, String>() {{
                        put("message", "Invalid email or password");
                    }});
        } catch (LockedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new HashMap<String, String>() {{
                        put("message", "Account is locked. Please try again later or contact support.");
                    }});
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Registers a new user with customer details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Email already in use or invalid data")
    })
    @SecurityRequirements()
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        // Check if email exists
        if (userService.isEmailTaken(registerRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new HashMap<String, String>() {{
                        put("message", "Email is already in use");
                    }});
        }

        // Create or get customer
        Customer customer;
        if (registerRequest.getCustomerId() != null) {
            customer = customerRepository.findById(registerRequest.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + registerRequest.getCustomerId()));
        } else {
            customer = new Customer();
            customer.setFirstName(registerRequest.getFirstName());
            customer.setLastName(registerRequest.getLastName());
            customer.setEmail(registerRequest.getEmail());
            customer.setPhone(registerRequest.getPhone());
            customer.setCreatedAt(Instant.now());
            customer = customerRepository.save(customer);
        }

        // Create user with ROLE_USER by default
        User user = userService.createUser(customer, registerRequest.getEmail(), registerRequest.getPassword(), "USER");

        // Send confirmation email
        emailService.sendRegistrationConfirmationEmail(user.getEmail(), customer.getFirstName());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully!");
        response.put("userId", user.getId());
        response.put("email", user.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Refreshes an expired JWT token using a refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refreshed successfully", 
                content = @Content(schema = @Schema(implementation = TokenRefreshResponse.class))),
        @ApiResponse(responseCode = "403", description = "Invalid or expired refresh token")
    })
    @SecurityRequirements()
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromEmail(user.getEmail());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Logs out the current user by invalidating their refresh tokens")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logged out successfully"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<?> logoutUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = userDetails.getId();
        refreshTokenService.deleteByUserId(userId);
        
        return ResponseEntity.ok(new HashMap<String, String>() {{
            put("message", "Log out successful!");
        }});
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot password", description = "Sends a password reset email to the user's email address")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset email sent if email exists")
    })
    @SecurityRequirements()
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody PasswordResetRequest request) {
        return userService.findUserByEmail(request.getEmail())
                .map(user -> {
                    // Create password reset token
                    String token = passwordResetService.createPasswordResetToken(user).getToken();
                    
                    // Send password reset email
                    emailService.sendPasswordResetEmail(user.getEmail(), token);
                    
                    return ResponseEntity.ok(new HashMap<String, String>() {{
                        put("message", "Password reset email sent successfully");
                    }});
                })
                .orElse(ResponseEntity.ok(new HashMap<String, String>() {{
                    // Don't reveal if email exists or not for security reasons
                    put("message", "If your email is registered, you will receive a password reset link");
                }}));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Resets the user's password using a token sent via email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired password reset token")
    })
    @SecurityRequirements()
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordChangeRequest request) {
        if (!passwordResetService.validatePasswordResetToken(request.getToken())) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("message", "Invalid or expired password reset token");
            }});
        }
        
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        
        return ResponseEntity.ok(new HashMap<String, String>() {{
            put("message", "Password has been reset successfully");
        }});
    }
} 