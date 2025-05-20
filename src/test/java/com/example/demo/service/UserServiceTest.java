package com.example.demo.service;

import com.example.demo.dto.SignupRequest;
import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import com.example.demo.repositories.RoleRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.service.impl.UserServiceImpl;
import com.example.demo.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Role userRole;
    private Role adminRole;

    @BeforeEach
    public void setup() {
        testUser = TestDataFactory.createTestUser();
        
        userRole = new Role();
        userRole.setId(1);
        userRole.setName("ROLE_USER");
        
        adminRole = new Role();
        adminRole.setId(2);
        adminRole.setName("ROLE_ADMIN");
    }

    @Test
    public void testGetUserById_Found() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(userRepository).findById(1L);
    }

    @Test
    public void testGetUserById_NotFound() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.findById(999L));
        verify(userRepository).findById(999L);
    }

    @Test
    public void testRegisterUser_Success() {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("newuser");
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setFirstName("New");
        signupRequest.setLastName("User");
        
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(10L);
            return savedUser;
        });

        // Act
        User result = userService.registerUser(signupRequest);

        // Assert
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("newuser@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_USER")));
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testRegisterUser_UsernameExists() {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("existinguser");
        signupRequest.setEmail("new@example.com");
        
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> 
            userService.registerUser(signupRequest)
        );
        assertTrue(exception.getMessage().contains("Username is already taken"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testUpdateUserProfile_Success() {
        // Arrange
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("Name");
        updatedUser.setEmail("updated@example.com");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        User result = userService.updateUser(1L, updatedUser);

        // Assert
        assertNotNull(result);
        assertEquals("Updated", result.getFirstName());
        assertEquals("Name", result.getLastName());
        assertEquals("updated@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testDeleteUser_Success() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository).deleteById(1L);
    }

    @Test
    public void testAddRole_Success() {
        // Arrange
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        testUser.setRoles(roles);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userService.addRoleToUser(1L, "ROLE_ADMIN");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getRoles().size());
        assertTrue(result.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN")));
        verify(userRepository).save(testUser);
    }
} 