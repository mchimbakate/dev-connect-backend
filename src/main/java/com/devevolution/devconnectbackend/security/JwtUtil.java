package com.devevolution.devconnectbackend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {
    private static final String SECRET_KEY_BASE64 = System.getenv("JWT_SECRET_KEY");

    private static final Key SECRET_KEY;

    static {
        if (SECRET_KEY_BASE64 == null || SECRET_KEY_BASE64.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET_KEY environment variable is missing!");
        }
        SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY_BASE64));
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }
}
