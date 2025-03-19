package com.app.users.auth.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    public String generateToken(String username, Long userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey)), SignatureAlgorithm.HS256)
                .compact();
    }


    public Claims extractClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(Base64.getDecoder().decode(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public String extractUsername(String token) {
        Claims claims = extractClaims(token);
        return claims.getSubject();
    }

    public Long extractUserId(String token) {
        try {
            Claims claims = extractClaims(token);
            System.out.println("Extracted claims: " + claims);
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            System.err.println("Error extracting userId: " + e.getMessage());
            return null;
        }
    }

    public String extractRole(String token) {
        try {
            Claims claims = extractClaims(token);
            System.out.println("Extracted claims: " + claims);
            return claims.get("role", String.class);
        } catch (Exception e) {
            System.err.println("Error extracting role: " + e.getMessage());
            return null;
        }
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token, String username) {
        return username.equals(extractUsername(token)) && !isTokenExpired(token);

    }
}

