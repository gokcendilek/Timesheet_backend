package com.aksigorta.timesheet.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @Mock
    private JwtUtil mockJwtUtil;

    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userDetails = new User("exampleUser", "password123", java.util.Collections.emptyList());
    }

    @Test
    public void testGenerateToken() {
        when(mockJwtUtil.generateToken(anyString(), anyString())).thenReturn("dummyToken");

        String token = mockJwtUtil.generateToken(userDetails.getUsername(), "ADMIN");

        assertNotNull(token);
        assertEquals("dummyToken", token);
    }


    @Test
    public void testExtractUsername() {
        when(mockJwtUtil.extractUsername("dummyToken")).thenReturn("exampleUser");
        assertEquals("exampleUser", mockJwtUtil.extractUsername("dummyToken"));
    }

    @Test
    public void testTokenValidation() {
        when(mockJwtUtil.isTokenValid("dummyToken", userDetails)).thenReturn(true);
        assertTrue(mockJwtUtil.isTokenValid("dummyToken", userDetails));
    }

    @Test
    public void testTokenExpiration() {
        when(mockJwtUtil.isTokenValid("expiredToken", userDetails)).thenReturn(false);
        assertFalse(mockJwtUtil.isTokenValid("expiredToken", userDetails));
    }
}
