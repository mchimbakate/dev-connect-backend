package com.devevolution.devconnectbackend.controller;

import com.devevolution.devconnectbackend.model.User;
import com.devevolution.devconnectbackend.security.JwtUtil;
import com.devevolution.devconnectbackend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();  // We need this to validate password
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            // Log the registration attempt
            logger.info("Registering user: {}", user.getEmail());

            User newUser = userService.registerUser(user.getFirstName(),user.getLastName(),user.getEmail(), user.getPassword());
            String token = jwtUtil.generateToken(newUser.getEmail());

            return ResponseEntity.ok(token);
        } catch (Exception e) {
            logger.error("Registration error: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            // Log the login attempt
            logger.info("Login attempt for user: {}", user.getEmail());

            // Find user by email
            User existingUser = userService.findByEmail(user.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Compare the passwords using BCryptPasswordEncoder
            if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
                logger.error("Invalid credentials for user: {}", user.getEmail());
                return ResponseEntity.badRequest().body("Invalid credentials");
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(existingUser.getEmail());

            // Create a response object containing both token and user data
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);

            // Create a user data object (exclude sensitive data like password)
            Map<String, Object> userData = new HashMap<>();
            userData.put("firstName", existingUser.getFirstName());
            userData.put("lastName", existingUser.getLastName());
            userData.put("email", existingUser.getEmail());
            userData.put("id", existingUser.getId());

            response.put("user", userData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Login error: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
        }
    }

}