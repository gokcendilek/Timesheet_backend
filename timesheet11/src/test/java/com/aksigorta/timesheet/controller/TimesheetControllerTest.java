package com.aksigorta.timesheet.controller;


import com.aksigorta.timesheet.model.Timesheet;
import com.aksigorta.timesheet.model.User;
import com.aksigorta.timesheet.service.TimesheetService;
import com.aksigorta.timesheet.service.UserService;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TimesheetControllerTest {


    @Mock
    private TimesheetService timesheetService;

    @Mock
    private UserService userService;

    @Mock
    private Principal mockPrincipal;

    @InjectMocks
    private TimesheetController timesheetController;

    private MockMvc mockMvc;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(timesheetController).build();
    }

    @Test
    void testCreateTimesheet_ReturnsCreated() throws Exception {
        User user = new User();
        user.setUsername("john");

        Timesheet timesheet = new Timesheet();
        timesheet.setDescription("Worked on project");
        timesheet.setDate(LocalDate.of(2023, 8, 20));
        timesheet.setStartTime(LocalTime.of(9, 0));
        timesheet.setEndTime(LocalTime.of(17, 0));

        when(mockPrincipal.getName()).thenReturn("john");
        when(userService.findByUsername("john")).thenReturn(user);
        when(timesheetService.saveTimesheet(any(Timesheet.class))).thenReturn(timesheet);

        mockMvc.perform(post("/api/timesheets")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"Worked on project\", \"startTime\": \"09:00\", \"endTime\": \"17:00\", \"date\": \"2023-08-20\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Worked on project"))
                .andExpect(jsonPath("$.startTime").value("09:00"))
                .andExpect(jsonPath("$.endTime").value("17:00"))
                .andExpect(jsonPath("$.date").value("2023-08-20"));
    }


    @Test
    void testGetTimesheets_ReturnsOk() throws Exception {
        User user = new User();
        user.setUsername("john");

        Timesheet timesheet = new Timesheet();
        timesheet.setDescription("Worked on project");
        timesheet.setDate(LocalDate.now());

        when(mockPrincipal.getName()).thenReturn("john");
        when(userService.findByUsername("john")).thenReturn(user);
        when(timesheetService.findTimesheets(user, null, null)).thenReturn(List.of(timesheet));

        mockMvc.perform(get("/api/timesheets")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Worked on project"));
    }

    @Test
    void testUpdateTimesheet_ReturnsOk() throws Exception {
        User user = new User();
        user.setUsername("john");

        Timesheet timesheet = new Timesheet();
        timesheet.setId(1L);
        timesheet.setDescription("Updated project");
        timesheet.setStartTime(LocalTime.of(9, 0));
        timesheet.setEndTime(LocalTime.of(17, 0));
        timesheet.setDate(LocalDate.of(2023, 8, 20));

        when(mockPrincipal.getName()).thenReturn("john");
        when(userService.findByUsername("john")).thenReturn(user);
        when(timesheetService.findByUserAndTimesheetId(user, 1L)).thenReturn(timesheet);
        when(timesheetService.updateTimesheet(any(Timesheet.class))).thenReturn(timesheet);

        mockMvc.perform(put("/api/timesheets/1")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"Updated project\", \"startTime\": \"09:00\", \"endTime\": \"17:00\", \"date\": \"2023-08-20\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated project"))
                .andExpect(jsonPath("$.startTime").value("09:00"))
                .andExpect(jsonPath("$.endTime").value("17:00"))
                .andExpect(jsonPath("$.date").value("2023-08-20"));

    }


    @Test
    void testExportTimesheetsToCsv_ReturnsCsv() throws Exception {
        User user = new User();
        user.setUsername("john");

        Timesheet timesheet = new Timesheet();
        timesheet.setDate(LocalDate.of(2023, 8, 20));
        timesheet.setStartTime(LocalDate.now().atTime(9, 0).toLocalTime());
        timesheet.setEndTime(LocalDate.now().atTime(17, 0).toLocalTime());
        timesheet.setDescription("Worked on project");

        when(mockPrincipal.getName()).thenReturn("john");
        when(userService.findByUsername("john")).thenReturn(user);
        when(timesheetService.findTimesheets(user, null, null)).thenReturn(List.of(timesheet));

        mockMvc.perform(get("/api/timesheets/export/csv")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=timesheets.csv"))
                .andExpect(content().string("Date,Start Time,End Time,Description\n" +
                        "2023-08-20,09:00,17:00,Worked on project\n"));
    }
}

