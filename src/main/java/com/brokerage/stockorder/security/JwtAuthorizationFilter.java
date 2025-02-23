package com.brokerage.stockorder.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private static final String BASIC_AUTH_PREFIX = "Basic ";
    private static final String BEARER_AUTH_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");
            log.debug("Processing request for URL: {} with auth header: {}", 
                    request.getRequestURI(),
                    authHeader != null ? authHeader.substring(0, Math.min(10, authHeader.length())) + "..." : "null");

            // Basic auth check
            if (authHeader != null && authHeader.startsWith(BASIC_AUTH_PREFIX)) {
                log.debug("Basic auth detected, delegating to basic auth provider");
                filterChain.doFilter(request, response);
                return;
            }

            // JWT token check
            if (authHeader != null && authHeader.startsWith(BEARER_AUTH_PREFIX)) {
                String token = authHeader.substring(BEARER_AUTH_PREFIX.length());
                log.debug("JWT token detected, validating...");

                if (jwtTokenProvider.validateToken(token)) {
                    log.debug("JWT token is valid, getting authentication...");
                    Authentication auth = jwtTokenProvider.getAuthentication(token);
                    if (auth != null) {
                        log.debug("Setting authentication in context for user: {}", auth.getName());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    } else {
                        log.warn("Authentication object is null after JWT validation");
                    }
                } else {
                    log.warn("JWT token validation failed");
                }
            } else {
                log.debug("No JWT token found in request");
            }

            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            log.error("Authentication failed: {}", ex.getMessage(), ex);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authentication failed: " + ex.getMessage());
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/v1/auth/login") ||
               path.startsWith("/api/v1/auth/register");
    }
}