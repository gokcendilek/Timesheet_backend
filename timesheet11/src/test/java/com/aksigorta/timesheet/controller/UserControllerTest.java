package com.aksigorta.timesheet.controller;

import com.aksigorta.timesheet.model.Role;
import com.aksigorta.timesheet.model.User;
import com.aksigorta.timesheet.security.JwtUtil;
import com.aksigorta.timesheet.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.validation.ValidationException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testRegisterUser_Success() {
        User newUser = new User();
        newUser.setUsername("testUser");
        newUser.setEmail("testUser@example.com");
        newUser.setPassword("Test@123");

        when(userService.isUserExist(newUser.getUsername(), newUser.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        ResponseEntity<String> response = userController.registerUser(newUser);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User registered successfully", response.getBody());
        verify(userService, times(1)).saveUser(newUser);
    }


    @Test
    void testRegisterUser_UserAlreadyExists() {
        User newUser = new User();
        newUser.setUsername("existingUser");
        newUser.setEmail("existingUser@example.com");

        when(userService.isUserExist(newUser.getUsername(), newUser.getEmail())).thenReturn(true);

        ResponseEntity<String> response = userController.registerUser(newUser);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User already exists", response.getBody());
        verify(userService, never()).saveUser(any(User.class));
    }


    @Test
    void testRegisterUser_ValidationException() {
        User newUser = new User();
        newUser.setUsername("invalidUser");
        newUser.setEmail("invalidUser@example.com");

        when(userService.isUserExist(any(), any())).thenThrow(new ValidationException("Validation Error"));

        ResponseEntity<String> response = userController.registerUser(newUser);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation Error", response.getBody());
    }


    @Test
    void testRegisterUser_UnexpectedError() {
        User newUser = new User();
        newUser.setUsername("testUser");
        newUser.setEmail("testUser@example.com");

        when(userService.isUserExist(anyString(), anyString())).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<String> response = userController.registerUser(newUser);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred", response.getBody());
    }


    @Test
    void testLoginUser_Success() {
        User loginUser = new User();
        loginUser.setUsername("testUser");
        loginUser.setPassword("Test@123");

        User user = new User();
        user.setUsername("testUser");
        user.setPassword("encodedPassword");
        user.setRole(Role.ADMIN);

        when(userService.findByUsername("testUser")).thenReturn(user);
        when(passwordEncoder.matches("Test@123", user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user.getUsername(), user.getRole().name())).thenReturn("jwtToken");

        ResponseEntity<Map<String, String>> response = userController.loginUser(loginUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwtToken", response.getBody().get("token"));
    }


    @Test
    void testLoginUser_UserNotFound() {
        User loginUser = new User();
        loginUser.setUsername("nonExistentUser");
        loginUser.setPassword("Test@123");

        when(userService.findByUsername("nonExistentUser")).thenReturn(null);

        ResponseEntity<Map<String, String>> response = userController.loginUser(loginUser);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }


    @Test
    void testLoginUser_IncorrectPassword() {
        User loginUser = new User();
        loginUser.setUsername("testUser");
        loginUser.setPassword("Test@123");

        User user = new User();
        user.setUsername("testUser");
        user.setPassword("encodedPassword");

        when(userService.findByUsername("testUser")).thenReturn(user);
        when(passwordEncoder.matches("Test@123", user.getPassword())).thenReturn(false);

        ResponseEntity<Map<String, String>> response = userController.loginUser(loginUser);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }
}
