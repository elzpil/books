package com.app.books.auth.util;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;


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
        return extractClaims(token).get("userId", Long.class);
    }
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token, String username) {
        if (username == null) {
            log.error("Token validation failed: extracted username is null");
            return false;
        }

        final String extractedUsername = extractUsername(token);
        if (extractedUsername == null) {
            log.error("Token validation failed: extracted username is null");
            return false;
        }

        return extractedUsername.equals(username) && !isTokenExpired(token);
    }

}

