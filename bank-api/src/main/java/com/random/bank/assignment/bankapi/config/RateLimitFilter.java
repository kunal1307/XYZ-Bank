package com.random.bank.assignment.bankapi.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
public class RateLimitFilter implements Filter {

    private final Bucket bucket;

    public RateLimitFilter() {
        Bandwidth limit = Bandwidth.classic(
                2,
                Refill.greedy(2, Duration.ofSeconds(1))
        );

        this.bucket = Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();

        // Swagger makes multiple internal calls, so it is excluded from the API rate limit.
        if (isSwaggerRequest(path)) {
            chain.doFilter(request, response);
            return;
        }

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(429);
        httpResponse.setContentType("application/json");
        httpResponse.getWriter().write("""
                {
                  "status": 429,
                  "message": "Too many requests. Please try again later."
                }
                """);
    }

    private boolean isSwaggerRequest(String path) {
        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs");
    }
}
