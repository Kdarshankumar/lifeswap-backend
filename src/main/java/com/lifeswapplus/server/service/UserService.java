package com.lifeswapplus.server.service;

import com.lifeswapplus.server.model.User;
import com.lifeswapplus.server.repository.UserRepository;
import com.lifeswapplus.server.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ✅ GET ALL USERS
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ✅ GET USER BY ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // ✅ REGISTER USER
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // ✅ JWT EMAIL EXTRACT
    public String extractEmailFromToken(String token) {
        return jwtUtil.extractUsername(token);
    }

    // ✅ GET USER BY EMAIL
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ DELETE USER
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // ✅ SAVE / UPDATE USER
    public User saveUser(User user) {
        return userRepository.save(user);
    }
}