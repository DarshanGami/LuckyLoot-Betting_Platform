package com.example.api_gateway.filter;

import com.example.api_gateway.util.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class GatewayJwtFilter implements Filter {

    @Autowired
    private JwtUtil jwtUtil;

    private static final List<String> PUBLIC_URLS = Arrays.asList(
            "/gateway/auth/register",
            "/gateway/auth/login",
            "/gateway/auth/verify"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String path = request.getRequestURI();

        // Skip JWT check for public endpoints
        if (PUBLIC_URLS.contains(path)) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token)) {
                String userId = jwtUtil.extractUserId(token);
                String email = jwtUtil.extractEmail(token);

                request.setAttribute("userId", userId);
                request.setAttribute("email", email);

                chain.doFilter(request, response);
                return;
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
                return;
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\": \"Authorization token missing\"}");
    }
}
