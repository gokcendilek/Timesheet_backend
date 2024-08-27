package com.aksigorta.timesheet.security;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import java.io.PrintWriter;
import java.io.StringWriter;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


class CustomAccessDeniedHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private CustomAccessDeniedHandler accessDeniedHandler;
    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        accessDeniedHandler = new CustomAccessDeniedHandler();
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void whenAccessDenied_thenSetForbiddenStatusAndWriteMessage() throws Exception {
        AccessDeniedException accessDeniedException = new AccessDeniedException("Test Message");

        accessDeniedHandler.handle(request, response, accessDeniedException);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("Access Denied: Test Message"));
    }
}

