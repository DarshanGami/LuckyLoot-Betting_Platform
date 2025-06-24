package com.example.auth_service.filter;

import com.example.auth_service.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter implements Filter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String path = request.getRequestURI();

        // ✅ Allow unauthenticated access to public endpoints
        if (path.startsWith("/auth/register") ||
                path.startsWith("/auth/login") ||
                path.startsWith("/auth/verify")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                if(jwtUtil.validateToken(token)) {
                    String email = jwtUtil.extractEmail(token);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    chain.doFilter(request, response); // ✅ continue the request
                    return;
                }
                else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("""
                    {
                      "error": "Invalid or expired token"
                    }
                """);
                    return;
                }
            }
            catch (ExpiredJwtException e) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write("""
                {
                  "error": "Session expired. Please log in again."
                }
            """);
                return;
            }

            catch (SignatureException | MalformedJwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("""
                    {
                      "error": "Invalid token."
                    }
                """);
                return;
            }

        }

        // 🔴 If no token and not a public path → reject
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("""
            {
              "error": "Authorization token missing"
            }
        """);

    }
}