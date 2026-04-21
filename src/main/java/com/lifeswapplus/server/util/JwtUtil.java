package com.lifeswapplus.server.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final SecretKey signingKey;

    public JwtUtil() {
        this.signingKey = JwtKeyManager.loadOrCreateKey();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        try {
            Date exp = extractExpiration(token);
            return exp.before(new Date());
        } catch (Exception ex) {
            return true;
        }
    }

    // generate token subject = email
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 10)) // 10 hours
                .signWith(signingKey)
                .compact();
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername;
        try {
            extractedUsername = extractUsername(token);
        } catch (Exception ex) {
            return false;
        }
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
