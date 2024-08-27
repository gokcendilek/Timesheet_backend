package com.aksigorta.timesheet.security;
import com.aksigorta.timesheet.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.util.Random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;





@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private SecurityConfig securityConfig;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void contextLoads() {
        assertThat(securityConfig).isNotNull();
    }

    @Test
    void securityBeansAreLoaded() {
        assertThat(context.getBean(AuthenticationManager.class)).isNotNull();
        assertThat(context.getBean(PasswordEncoder.class)).isNotNull();
        assertThat(context.getBean(SecurityFilterChain.class)).isNotNull();
    }

    @Test
    void passwordEncoderIsBCrypt() {
        assertThat(passwordEncoder).isNotNull();
        assertThat(passwordEncoder).isInstanceOf(PasswordEncoder.class);
    }

    @Test
    void authenticationManagerIsConfigured() throws Exception {
        assertThat(authenticationManager).isNotNull();
    }



    @Test
    void shouldPermitAccessToPublicUrls() throws Exception {
        Random random = new Random();
        String uniqueUsername = "user" + random.nextInt(100000);
        String uniqueEmail = "user" + random.nextInt(100000) + "@example.com";

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + uniqueUsername + "\", \"email\":\"" + uniqueEmail + "\", \"password\":\"Test@123\", \"role\":\"USER\"}"))
                .andDo(print())
                .andExpect(status().isCreated());



        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + uniqueUsername + "\", \"password\":\"Test@123\"}"))
                .andExpect(status().isOk());

    }


    @Test
    void shouldDenyAccessToProtectedUrlsWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/admin/some-protected-endpoint"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldAllowAccessToAdminWithProperRole() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDenyAccessToAdminEndpointForNonAdminUsers() throws Exception {
        mockMvc.perform(get("/api/admin/some-protected-endpoint")
                        .with(SecurityMockMvcRequestPostProcessors.user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }
}
