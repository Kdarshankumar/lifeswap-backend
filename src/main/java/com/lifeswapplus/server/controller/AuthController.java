package com.lifeswapplus.server.controller;

import com.lifeswapplus.server.model.User;
import com.lifeswapplus.server.repository.UserRepository;
import com.lifeswapplus.server.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register endpoint
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {

            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body("Email already registered");
            }

            // ✅ Ensure required fields
            if (user.getName() == null || user.getEmail() == null || user.getPassword() == null) {
                return ResponseEntity.badRequest().body("Missing required fields");
            }

            // ✅ Set role (important)
            user.setRole("USER");

            // ✅ Encode password
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // 🔥 SAVE (this is where crash happens)
            userRepository.save(user);

            return ResponseEntity.ok("User registered successfully");

        } catch (Exception e) {
            e.printStackTrace();   // 🔥 VERY IMPORTANT
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> userMap) {
        String email = userMap.get("email");
        String password = userMap.get("password");

        Map<String, Object> response = new HashMap<>();

        var userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(401).body(response);
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(401).body(response);
        }

        // Generate JWT Token
        String token = jwtUtil.generateToken(email);
        response.put("message", "Login successful");
        response.put("token", token);
        response.put("user", user);

        return ResponseEntity.ok(response);
    }
}
