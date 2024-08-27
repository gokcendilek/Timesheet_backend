package com.aksigorta.timesheet.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSearchUsers_Success() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .with(user("admin").password("Password123!").roles("ADMIN"))
                        .param("username", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testExportTimesheetsToCsv_Success() throws Exception {
        mockMvc.perform(get("/api/admin/timesheets/export/csv")
                        .with(user("admin").password("Password123!").roles("ADMIN"))
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=timesheets.csv"));
    }
}
