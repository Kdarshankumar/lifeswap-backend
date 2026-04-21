package com.lifeswapplus.server.controller;

import com.lifeswapplus.server.model.User;
import com.lifeswapplus.server.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // ✅ TEST ENDPOINT
    @GetMapping("/test")
    public Map<String, String> test() {
        return Map.of(
                "status", "Authorized!",
                "message", "Your token is valid"
        );
    }

    // ✅ GET ALL USERS
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // ✅ GET USER BY ID
    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // ✅ GET CURRENT USER (JWT)
    @GetMapping("/me")
    public User getCurrentUser(@RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String email = userService.extractEmailFromToken(token);

        return userService.getUserByEmail(email);
    }

    // ✅ 🔥 UPDATE PROFILE (FINAL CORRECT)
    @PutMapping("/updateProfile")
    public User updateProfile(@RequestBody User updatedUser) {

        return userService.getUserById(updatedUser.getId())
                .map(user -> {
                    user.setName(updatedUser.getName());   // ✅ CORRECT
                    user.setEmail(updatedUser.getEmail());
                    return userService.saveUser(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ DELETE USER
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}