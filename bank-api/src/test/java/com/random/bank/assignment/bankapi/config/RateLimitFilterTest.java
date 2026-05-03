package com.random.bank.assignment.bankapi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;

import static org.mockito.Mockito.*;

class RateLimitFilterTest {

    @Test
    void shouldAllowSwaggerRequestsWithoutRateLimit() throws Exception {
        RateLimitFilter filter = new RateLimitFilter();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/swagger-ui/index.html");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).setStatus(429);
    }

    @Test
    void shouldBlockRequestsAfterLimitIsExceeded() throws Exception {
        RateLimitFilter filter = new RateLimitFilter();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/register");
        when(response.getWriter()).thenReturn(mock(PrintWriter.class));

        filter.doFilter(request, response, chain);
        filter.doFilter(request, response, chain);
        filter.doFilter(request, response, chain);

        verify(chain, times(2)).doFilter(request, response);
        verify(response).setStatus(429);
        verify(response).setContentType("application/json");
    }
}