package com.aksigorta.timesheet.service;
import com.aksigorta.timesheet.model.User;
import com.aksigorta.timesheet.repository.UserRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Mockito'nun mock nesneleri başlatması için
    }

    @Test
    public void testFindByUsername_UserExists() {
        User user = new User();
        user.setUsername("john");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        User result = userService.findByUsername("john");

        assertNotNull(result);
        assertEquals("john", result.getUsername());
    }

    @Test
    public void testFindByUsername_UserNotExists() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());

        User result = userService.findByUsername("john");

        assertNull(result);
    }

    @Test
    public void testIsUserExist_UserExists() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(new User()));

        boolean exists = userService.isUserExist("john", "john@example.com");

        assertTrue(exists);
    }

    @Test
    public void testIsUserExist_UserNotExists() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        boolean exists = userService.isUserExist("john", "john@example.com");

        assertFalse(exists);
    }

    @Test
    public void testRegisterUser_UserAlreadyExists() {
        User user = new User();
        user.setUsername("john");
        user.setEmail("john@example.com");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userService.registerUser(user);
        });

        assertEquals("User with this username or email already exists.", exception.getMessage());
    }

    @Test
    public void testRegisterUser_PasswordInvalid() {
        User user = new User();
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setPassword("weak");

        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userService.registerUser(user);
        });

        assertEquals("Password does not meet the required criteria.", exception.getMessage());
    }

    @Test
    public void testRegisterUser_Success() {
        User user = new User();
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setPassword("StrongP@ssw0rd");

        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("StrongP@ssw0rd")).thenReturn("encodedPassword");

        userService.registerUser(user);

        verify(userRepository, times(1)).save(user);
        assertEquals("encodedPassword", user.getPassword());
    }
}

