package com.aksigorta.timesheet.service;

import com.aksigorta.timesheet.model.Role;
import com.aksigorta.timesheet.model.Timesheet;
import com.aksigorta.timesheet.model.User;
import com.aksigorta.timesheet.repository.TimesheetRepository;
import com.aksigorta.timesheet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TimesheetRepository timesheetRepository;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSearchUsersByUsername() {
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setUsername("john");
        users.add(user);

        when(userRepository.findByUsernameContaining("john")).thenReturn(users);

        List<User> result = adminService.searchUsers("john", null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("john", result.get(0).getUsername());
        verify(userRepository, times(1)).findByUsernameContaining("john");
    }

    @Test
    public void testSearchUsersByEmail() {
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setEmail("john@example.com");
        users.add(user);

        when(userRepository.findByEmailContaining("john@example.com")).thenReturn(users);

        List<User> result = adminService.searchUsers(null, "john@example.com", null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("john@example.com", result.get(0).getEmail());
        verify(userRepository, times(1)).findByEmailContaining("john@example.com");
    }

    @Test
    public void testSearchUsersByRegistrationDate() {
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setRegistrationDate(LocalDate.of(2023, 8, 25));
        users.add(user);

        LocalDate registrationDate = LocalDate.of(2023, 8, 25);
        when(userRepository.findByRegistrationDate(registrationDate)).thenReturn(users);

        List<User> result = adminService.searchUsers(null, null, "2023-08-25");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(registrationDate, result.get(0).getRegistrationDate());
        verify(userRepository, times(1)).findByRegistrationDate(registrationDate);
    }

    @Test
    public void testSearchUsersWithInvalidRegistrationDate() {
        assertThrows(IllegalArgumentException.class, () -> {
            adminService.searchUsers(null, null, "invalid-date");
        });
    }

    @Test
    public void testSearchUsersWithEmptyCriteria() {
        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = adminService.searchUsers(null, null, null);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }



    @Test
    public void testSearchTimesheetsByUserId() {
        User user = new User();
        user.setId(1L);

        Timesheet timesheet = new Timesheet();
        timesheet.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(timesheetRepository.findByUserId(1L)).thenReturn(List.of(timesheet));

        List<Timesheet> result = adminService.searchTimesheets(1L, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(timesheetRepository, times(1)).findByUserId(1L);
    }

    @Test
    public void testExportUsersToCSV() throws IOException {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setRole(Role.ADMIN);

        when(userRepository.findAll()).thenReturn(List.of(user));

        ByteArrayInputStream csvStream = adminService.exportUsersToCSV();
        assertNotNull(csvStream);

        byte[] buffer = new byte[csvStream.available()];
        csvStream.read(buffer);
        String csvContent = new String(buffer);

        assertTrue(csvContent.contains("john"));
        assertTrue(csvContent.contains("john@example.com"));
    }

    @Test
    public void testExportTimesheetsToCsv() throws IOException {
        User user = new User();
        user.setUsername("john");

        Timesheet timesheet = new Timesheet();
        timesheet.setDate(LocalDate.now());
        timesheet.setStartTime(LocalDate.now().atTime(9, 0).toLocalTime());
        timesheet.setEndTime(LocalDate.now().atTime(17, 0).toLocalTime());
        timesheet.setDescription("Worked on project");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(timesheetRepository.findByUserAndDateBetween(user, LocalDate.MIN, LocalDate.MAX)).thenReturn(List.of(timesheet));

        ByteArrayInputStream csvStream = adminService.exportTimesheetsToCsv("john", null, null);
        assertNotNull(csvStream);

        byte[] buffer = new byte[csvStream.available()];
        csvStream.read(buffer);
        String csvContent = new String(buffer);

        assertTrue(csvContent.contains("Worked on project"));
    }

    @Test
    public void testExportTimesheetsToExcel() throws IOException {
        User user = new User();
        user.setUsername("john");

        Timesheet timesheet = new Timesheet();
        timesheet.setId(1L);
        timesheet.setDate(LocalDate.now());
        timesheet.setStartTime(LocalDate.now().atTime(9, 0).toLocalTime());
        timesheet.setEndTime(LocalDate.now().atTime(17, 0).toLocalTime());
        timesheet.setDescription("Worked on project");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(timesheetRepository.findByUserAndDateBetween(user, LocalDate.MIN, LocalDate.MAX)).thenReturn(List.of(timesheet));

        ByteArrayInputStream excelStream = adminService.exportTimesheetsToExcel("john", null, null);
        assertNotNull(excelStream);


        assertTrue(excelStream.available() > 0);
    }
}
