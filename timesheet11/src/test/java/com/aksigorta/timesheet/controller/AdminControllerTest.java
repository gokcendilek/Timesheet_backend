package com.aksigorta.timesheet.controller;

import com.aksigorta.timesheet.model.Timesheet;
import com.aksigorta.timesheet.model.User;
import com.aksigorta.timesheet.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Test
    void testSearchUsers_ReturnsOk() throws Exception {
        User user = new User();
        user.setUsername("john");

        when(adminService.searchUsers("john", null, null)).thenReturn(List.of(user));

        mockMvc.perform(get("/api/admin/users")
                        .param("username", "john")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("john"));
    }

    @Test
    void testSearchUsers_NoParams_ReturnsOk() throws Exception {
        when(adminService.searchUsers(null, null, null)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testSearchTimesheets_ReturnsOk() throws Exception {
        User user = new User();
        user.setUsername("john");

        Timesheet timesheet = new Timesheet();
        timesheet.setUser(user);
        timesheet.setDate(LocalDate.of(2023, 8, 20));
        timesheet.setStartTime(LocalDate.now().atTime(9, 0).toLocalTime());
        timesheet.setEndTime(LocalDate.now().atTime(17, 0).toLocalTime());
        timesheet.setDescription("Worked on project");

        when(adminService.searchTimesheets(1L, "2023-08-20")).thenReturn(List.of(timesheet));

        mockMvc.perform(get("/api/admin/timesheets")
                        .param("userId", "1")
                        .param("date", "2023-08-20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user.username").value("john"))
                .andExpect(jsonPath("$[0].date").value("2023-08-20"))
                .andExpect(jsonPath("$[0].description").value("Worked on project"));

    }


    @Test
    void testSearchTimesheets_ReturnsEmptyList() throws Exception {
        when(adminService.searchTimesheets(1L, "2023-08-20")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/timesheets")
                        .param("userId", "1")
                        .param("date", "2023-08-20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testExportTimesheetsToCsv_ReturnsCsv() throws Exception {
        User user = new User();
        user.setUsername("john");

        Timesheet timesheet = new Timesheet();
        timesheet.setUser(user);
        timesheet.setDate(LocalDate.of(2023, 8, 20));
        timesheet.setStartTime(LocalDate.now().atTime(9, 0).toLocalTime());
        timesheet.setEndTime(LocalDate.now().atTime(17, 0).toLocalTime());
        timesheet.setDescription("Worked on project");

        when(adminService.searchTimesheets(1L, "2023-08-20")).thenReturn(List.of(timesheet));

        mockMvc.perform(get("/api/admin/timesheets/export/csv")
                        .param("userId", "1")
                        .param("date", "2023-08-20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=timesheets.csv"))
                .andExpect(content().string("UserName,Date,Start Time,End Time,Description\n" +
                        "john,2023-08-20,09:00,17:00,Worked on project\n"));
    }

    @Test
    void testExportTimesheetsToCsv_NoTimesheets() throws Exception {
        when(adminService.searchTimesheets(1L, "2023-08-20")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/timesheets/export/csv")
                        .param("userId", "1")
                        .param("date", "2023-08-20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
