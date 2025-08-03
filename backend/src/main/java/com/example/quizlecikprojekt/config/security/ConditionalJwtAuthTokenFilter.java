package com.example.quizlecikprojekt.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ConditionalJwtAuthTokenFilter extends OncePerRequestFilter {

    private final JwtAuthTokenFilter delegate;
    private final String excludedEndpoint;

    public ConditionalJwtAuthTokenFilter(JwtAuthTokenFilter delegate, String excludedEndpoint) {
        this.delegate = delegate;
        this.excludedEndpoint = excludedEndpoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getRequestURI().startsWith(excludedEndpoint)) {
            filterChain.doFilter(request, response);
            return;
        }

        delegate.doFilter(request, response, filterChain);
    }
}
