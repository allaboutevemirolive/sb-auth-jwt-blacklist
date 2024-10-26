package com.allaboutevemirolive.sb_auth_jwt_blacklist.config.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.allaboutevemirolive.sb_auth_jwt_blacklist.config.property.JwtProperties;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    private final JwtProperties jwtProperties;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenExpiration()))
                .signWith(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes()))
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }
}
