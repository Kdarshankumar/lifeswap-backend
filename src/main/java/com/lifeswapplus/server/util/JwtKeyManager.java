package com.lifeswapplus.server.util;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.file.*;
import java.util.Base64;

public class JwtKeyManager {

    private static final Path KEY_PATH = Paths.get("config", "jwt.key");

    public static SecretKey loadOrCreateKey() {
        try {
            if (Files.exists(KEY_PATH)) {
                String base64 = Files.readString(KEY_PATH).trim();
                byte[] keyBytes = Base64.getDecoder().decode(base64);
                return Keys.hmacShaKeyFor(keyBytes);
            } else {
                if (KEY_PATH.getParent() != null) {
                    Files.createDirectories(KEY_PATH.getParent());
                }

                SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
                String base64 = Base64.getEncoder().encodeToString(key.getEncoded());

                Path temp = Files.createTempFile("jwtkey", ".tmp");
                Files.writeString(temp, base64, StandardOpenOption.WRITE);
                Files.move(temp, KEY_PATH,
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);

                System.out.println("JWT key generated and saved to: " + KEY_PATH.toAbsolutePath());
                return key;
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load or create JWT key at "
                    + KEY_PATH.toAbsolutePath(), ex);
        }
    }
}
