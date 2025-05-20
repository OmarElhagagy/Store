package com.example.demo.security;

import com.example.demo.security.jwt.JwtUtils;
import com.example.demo.security.services.UserDetailsImpl;
import com.example.demo.util.TestDataFactory;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Authentication authentication;

    private UserDetailsImpl userDetails;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "testSecretKeyWhichIsLongEnoughToBeValidForTheAlgorithm");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3600000); // 1 hour
        userDetails = TestDataFactory.createUserDetails();
    }

    @Test
    public void testGenerateJwtToken() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Act
        String token = jwtUtils.generateJwtToken(authentication);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    public void testGetUserNameFromJwtToken() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtUtils.generateJwtToken(authentication);

        // Act
        String username = jwtUtils.getUserNameFromJwtToken(token);

        // Assert
        assertEquals("testuser", username);
    }

    @Test
    public void testValidateJwtToken_Valid() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtUtils.generateJwtToken(authentication);

        // Act & Assert
        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    public void testValidateJwtToken_Invalid() {
        // Act & Assert
        assertFalse(jwtUtils.validateJwtToken("invalidtoken"));
    }

    @Test
    public void testParseJwt() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");

        // Act
        String result = jwtUtils.parseJwt(request);

        // Assert
        assertEquals("validToken", result);
    }

    @Test
    public void testParseJwt_NoBearer() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("validToken");

        // Act
        String result = jwtUtils.parseJwt(request);

        // Assert
        assertNull(result);
    }

    @Test
    public void testParseJwt_NoHeader() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        String result = jwtUtils.parseJwt(request);

        // Assert
        assertNull(result);
    }
}