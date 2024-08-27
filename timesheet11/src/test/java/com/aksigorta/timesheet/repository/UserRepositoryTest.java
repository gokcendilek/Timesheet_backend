package com.aksigorta.timesheet.repository;

import com.aksigorta.timesheet.model.User;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @MockBean
    private UserRepository userRepository;

    @Test
    public void whenFindByUsername_thenReturnUser() {
        User mockUser = new User();
        mockUser.setUsername("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));

        Optional<User> found = userRepository.findByUsername("testUser");

        assertTrue(found.isPresent());
        assertEquals(found.get().getUsername(), "testUser");
        verify(userRepository).findByUsername("testUser");
    }

    @Test
    public void whenFindById_thenReturnUser() {
        User mockUser = new User();
        mockUser.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        Optional<User> found = userRepository.findById(1L);

        assertTrue(found.isPresent());
        assertEquals(found.get().getId(), 1L);
        verify(userRepository).findById(1L);
    }

    @Test
    public void whenFindByEmail_thenReturnUser() {
        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

        Optional<User> found = userRepository.findByEmail("test@example.com");

        assertTrue(found.isPresent());
        assertEquals(found.get().getEmail(), "test@example.com");
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    public void whenFindByRegistrationDate_thenReturnListOfUsers() {
        User mockUser = new User();
        LocalDate registrationDate = LocalDate.of(2023, 1, 1);
        mockUser.setRegistrationDate(registrationDate);
        when(userRepository.findByRegistrationDate(registrationDate)).thenReturn(Collections.singletonList(mockUser));

        List<User> found = userRepository.findByRegistrationDate(registrationDate);

        assertFalse(found.isEmpty());
        assertEquals(found.get(0).getRegistrationDate(), registrationDate);
        verify(userRepository).findByRegistrationDate(registrationDate);
    }
}
