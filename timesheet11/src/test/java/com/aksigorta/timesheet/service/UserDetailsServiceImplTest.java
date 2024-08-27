package com.aksigorta.timesheet.service;

import com.aksigorta.timesheet.model.Role;
import com.aksigorta.timesheet.model.User;
import com.aksigorta.timesheet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoadUserByUsername_UserExists() {

        User user = new User();
        user.setUsername("john");
        user.setPassword("password123");
        user.setRole(Role.USER);


        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));


        UserDetails userDetails = userDetailsService.loadUserByUsername("john");


        assertNotNull(userDetails);
        assertEquals("john", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {

        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());


        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("john");
        });
    }
}
