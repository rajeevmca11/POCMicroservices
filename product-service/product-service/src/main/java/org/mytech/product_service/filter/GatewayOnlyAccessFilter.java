package org.mytech.product_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class GatewayOnlyAccessFilter extends OncePerRequestFilter {

    private static final String SECRET = "my-super-secret-key-for-jwt-12345678";
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        // ✅ Bypass Swagger paths for local testing
        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
            System.out.println("🔓 Allowing Swagger path without JWT: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("X-Gateway-Token");
        if (!SECRET.equals(header)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
            response.getWriter().write("Access denied: Gateway-only access enforced.");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
