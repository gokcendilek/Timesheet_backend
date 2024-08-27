package com.aksigorta.timesheet.integration;

import com.aksigorta.timesheet.model.Timesheet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TimesheetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {

        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testCreateTimesheet_Success() throws Exception {
        Timesheet timesheet = new Timesheet();
        timesheet.setDate(LocalDate.now());
        timesheet.setStartTime(LocalTime.of(9, 0));
        timesheet.setEndTime(LocalTime.of(17, 0));
        timesheet.setDescription("Test Timesheet");

        mockMvc.perform(post("/api/timesheets")
                        .with(user("testuser").password("Password123!").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(timesheet)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Test Timesheet"));
    }

    @Test
    public void testGetTimesheets_Success() throws Exception {
        mockMvc.perform(get("/api/timesheets")
                        .with(user("testuser").password("Password123!").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }




    @Test
    public void testExportTimesheetsToCsv_Success() throws Exception {

        Timesheet timesheet = new Timesheet();
        timesheet.setDate(LocalDate.now());
        timesheet.setStartTime(LocalTime.of(9, 0));
        timesheet.setEndTime(LocalTime.of(17, 0));
        timesheet.setDescription("Test Timesheet");


        mockMvc.perform(post("/api/timesheets")
                        .with(user("testuser").password("Password123!").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(timesheet)))
                .andExpect(status().isCreated());


        mockMvc.perform(get("/api/timesheets/export/csv")
                        .with(user("testuser").password("Password123!").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=timesheets.csv"));
    }
}
