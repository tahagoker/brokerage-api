package com.brokerage.stockorder.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that handles JWT token authorization
 */
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

            // Basic auth check
            if (authHeader != null && authHeader.startsWith(BASIC_AUTH_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }

            // JWT token check
            if (authHeader != null && authHeader.startsWith(BEARER_AUTH_PREFIX)) {
                String token = authHeader.substring(BEARER_AUTH_PREFIX.length());

                if (jwtTokenProvider.validateToken(token)) {
                    Authentication auth = jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }

            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            logger.error("JWT Authentication failed: {}");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authentication failed");
        } finally {
            // İşlem bittikten sonra security context'i temizle
            SecurityContextHolder.clearContext();
        }
    }
/*
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/v1/auth/login") ||
            path.startsWith("/api/v1/auth/register");
    } */
}
/*
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
        String basicAuth = request.getHeader("Authorization");
        if (basicAuth != null && basicAuth.startsWith("Basic ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtTokenProvider.resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication auth = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}

 */