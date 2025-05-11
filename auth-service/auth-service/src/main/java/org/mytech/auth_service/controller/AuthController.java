package org.mytech.auth_service.controller;

import org.mytech.auth_service.model.AuthRequest;
import org.mytech.auth_service.model.AuthResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    // ✅ Use a secure, 32+ character key
    private final String secret = "my-super-secret-key-for-jwt-12345678";

    //@Value("${jwt.secret}")

    private final Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        // 1. Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                )
        );

        // 2. Generate JWT using proper method for JJWT 0.11.5
        String token = Jwts.builder()
                .setSubject(authRequest.getUsername())
                .claim("role","USER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(key, SignatureAlgorithm.HS256) // ✅ Correct usage
                .compact();

        // 3. Return token
        return new AuthResponse(token);
    }
}
