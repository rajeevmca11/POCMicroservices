package org.mytech.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import reactor.core.publisher.Mono;


import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtAuthGatewayFilterFactory.Config> {
    public JwtAuthGatewayFilterFactory() {
        super(Config.class);
        System.out.println("🔥 JwtAuthGatewayFilterFactory constructor called!");
    }


    private static final String SECRET = "my-super-secret-key-for-jwt-12345678";



    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().toString();

            // ✅ Bypass Swagger & OpenAPI docs
            if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
                System.out.println("🔓 Swagger path bypassed: " + path);
                return chain.filter(exchange);
            }

            HttpHeaders headers = exchange.getRequest().getHeaders();
            String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);

            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                        .parseClaimsJws(token)
                        .getBody();

                String subject = claims.getSubject();
                String role = claims.get("role", String.class);  // ✅ Extract role from JWT


                System.out.println("✅ JWT validated for: " + subject + ", role: " + role +"path: "+path);

                if (subject == null || subject.isEmpty()) {
                    throw new RuntimeException("Invalid token: subject is missing");
                }

                System.out.println("✅ JWT validated for: " + subject);

                // 🔐 Role-based check
                if (path.startsWith("/orders") && !"ADMIN".equals(role)) {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }

                exchange = exchange.mutate()
                        .request(builder -> builder
                                .header("X-Gateway-Token", SECRET) // ✅ add custom internal header
                                .headers(h -> h.remove("Authorization"))          // (optional) clean token
                        )
                        .build();



            } catch (JwtException | IllegalArgumentException e) {
                System.out.println("❌ JWT validation failed: " + e.getMessage());
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        };
    }
    public static class Config {
        public Config() {} // ✅ This constructor is REQUIRED
    }

    @PostConstruct
    public void init() {
        System.out.println("✅ JwtAuthGatewayFilterFactory registered");
    }


}
