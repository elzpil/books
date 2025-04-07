package com.app.users.auth.controller;

import com.app.users.auth.util.JwtTokenUtil;
import com.app.users.model.User;
import com.app.users.business.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        if (userService.existsByUsername(user.getEmail())) {  // Fix to check email instead
            return ResponseEntity.badRequest().body("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt password
        userService.saveUser(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {

        Optional<User> existingUserOptional = userService.findByEmail(user.getEmail());

        if (existingUserOptional.isEmpty()) {
            System.out.println("User not found");
            return ResponseEntity.badRequest().body(Map.of("message", "Incorrect username"));
        }

        User existingUser = existingUserOptional.get();
        System.out.println("Existing user email, id, role: " + existingUser.getEmail() + ", "
                + existingUser.getId() + ", " + existingUser.getRole());

        if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Incorrect password"));
        }

        String token = jwtTokenUtil.generateToken(existingUser.getEmail(), existingUser.getId(), existingUser.getRole());
        return ResponseEntity.ok(Map.of("token", token));
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout() {

        return ResponseEntity.ok("User logged out successfully");
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestHeader("Authorization") String oldToken) {
        String token = oldToken.replace("Bearer ", "");

        if (jwtTokenUtil.isTokenExpired(token)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token expired"));
        }

        String username = jwtTokenUtil.extractUsername(token);
        Long userId = jwtTokenUtil.extractUserId(token);
        String role = jwtTokenUtil.extractRole(token);

        String newToken = jwtTokenUtil.generateToken(username, userId, role);
        return ResponseEntity.ok(Map.of("token", newToken));
    }
}

