package com.aksigorta.timesheet.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import jakarta.servlet.FilterChain;
import java.util.Collections;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class JwtRequestFilterTest {

    @InjectMocks
    private JwtRequestFilter jwtRequestFilter;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        userDetails = new User("user", "password", Collections.emptyList());
    }

    @Test
    public void testJwtTokenAuthentication() throws Exception {
        String token = "Bearer dummyToken";
        request.addHeader("Authorization", token);

        when(jwtUtil.extractUsername("dummyToken")).thenReturn("user");
        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
        when(jwtUtil.isTokenValid("dummyToken", userDetails)).thenReturn(true);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(userDetailsService).loadUserByUsername("user");
        verify(jwtUtil).isTokenValid("dummyToken", userDetails);
        verify(filterChain).doFilter(request, response);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        UsernamePasswordAuthenticationToken actualAuthentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(actualAuthentication);
        assertEquals(userDetails.getUsername(), actualAuthentication.getName());
        assertTrue(actualAuthentication.getAuthorities().isEmpty());
    }

    @Test
    public void testJwtTokenAbsent() throws Exception {
        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
