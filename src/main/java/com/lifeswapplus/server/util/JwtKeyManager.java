package com.lifeswapplus.server.util;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class JwtKeyManager {

    private static final String SECRET = System.getenv("JWT_SECRET");

    public static SecretKey loadOrCreateKey() {

        if (SECRET == null || SECRET.isEmpty()) {
            throw new RuntimeException("JWT_SECRET environment variable is not set");
        }

        byte[] keyBytes = SECRET.getBytes(StandardCharsets.UTF_8);

        return Keys.hmacShaKeyFor(keyBytes);
    }
}