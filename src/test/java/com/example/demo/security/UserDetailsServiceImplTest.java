package com.example.demo.security;

import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.services.UserDetailsImpl;
import com.example.demo.security.services.UserDetailsServiceImpl;
import com.example.demo.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    public void testLoadUserByUsername_Success() {
        // Arrange
        User testUser = TestDataFactory.createTestUser();
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof UserDetailsImpl);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistentuser");
        });
        verify(userRepository).findByUsername("nonexistentuser");
    }
} 