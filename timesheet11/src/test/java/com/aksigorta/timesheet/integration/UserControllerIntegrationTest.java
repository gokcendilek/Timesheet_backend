package com.aksigorta.timesheet.integration;

import com.aksigorta.timesheet.model.Role;
import com.aksigorta.timesheet.model.User;
import com.aksigorta.timesheet.repository.TimesheetRepository;
import com.aksigorta.timesheet.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TimesheetRepository timesheetRepository;

    @BeforeEach
    public void setup() throws Exception {
        timesheetRepository.deleteAll();


        userRepository.deleteAll();

        objectMapper.registerModule(new JavaTimeModule());

        User existingUser = new User();
        existingUser.setUsername("existinguser");
        existingUser.setEmail("testuser3@example.com");
        existingUser.setPassword("Password123!");
        existingUser.setRole(Role.USER);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingUser)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        User user = new User();
        user.setUsername("testuser5");
        user.setEmail("testuser5@example.com");
        user.setPassword("Password123!");
        user.setRole(Role.USER);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    public void testRegisterUser_DuplicateEmail() throws Exception {
        User user = new User();
        user.setUsername("uniqueuser2");
        user.setEmail("testuser3@example.com");
        user.setPassword("Password123!");
        user.setRole(Role.USER);


        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User already exists"));
    }

    @Test
    public void testLoginUser_Success() throws Exception {
        User loginUser = new User();
        loginUser.setUsername("existinguser");
        loginUser.setPassword("Password123!");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    public void testLoginUser_Failure() throws Exception {
        User loginUser = new User();
        loginUser.setUsername("wronguser");
        loginUser.setPassword("wrongpassword");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUser)))
                .andExpect(status().isUnauthorized());
    }
}
