package edu.usc.csci310.project.configs;

import edu.usc.csci310.project.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthFilterTest {

    @InjectMocks
    private AuthFilter authFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }


    @Test
    void doFilterInternal_validToken_authenticationSet() throws Exception {
        String email = "test@example.com";
        String token = JwtUtil.generateToken(email);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(email, SecurityContextHolder.getContext().getAuthentication().getName());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities().isEmpty());
    }

    @Test
    void doFilterInternal_noAuthorizationHeader_doesNothing() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "Expected no authentication to be set");
    }

    @Test
    void doFilterInternal_authorizationHeaderNotBearer_doesNothing() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic abcdefg1234567");

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "Expected no authentication to be set");
    }

    @Test
    void doFilterInternal_invalidToken_clearContext() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }
}
