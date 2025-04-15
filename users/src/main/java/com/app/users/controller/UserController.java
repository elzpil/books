package com.app.users.controller;

import com.app.users.auth.util.JwtTokenUtil;
import com.app.users.business.service.UserService;
import com.app.users.dto.ChangePasswordRequest;
import com.app.users.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;

    public UserController(UserService userService, JwtTokenUtil jwtTokenUtil) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }
    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.saveUser(user));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        Optional<User> user = userService.getUserById(userId);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId,
                                           @RequestBody User updatedUser,
                                           @RequestHeader("Authorization") String token) {
        log.info("Updating user, token: " + token);
        if (!isAuthorized(token, userId)) {
            return ResponseEntity.status(403).build();  // Forbidden if not authorized
        }
        Optional<User> user = userService.updateUser(userId, updatedUser);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId,
                                           @RequestHeader("Authorization") String token) {
        if (!isAuthorized(token, userId)) {
            return ResponseEntity.status(403).build();  // Forbidden if not authorized
        }
        boolean deleted = userService.deleteUser(userId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    private boolean isAuthorized(String token, Long userId) {
        String cleanToken = token.replace("Bearer ", "");
        log.info(" Authorizing token: " + cleanToken);
        Long tokenUserId = jwtTokenUtil.extractUserId(cleanToken);
        String role = jwtTokenUtil.extractRole(cleanToken);
        log.info(" User id and role: {} {} ", tokenUserId, role);
        return tokenUserId.equals(userId) || "ADMIN".equals(role);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}/role")
    public ResponseEntity<User> updateUserRole(@PathVariable Long userId, @RequestParam String role) {
        Optional<User> user = userService.updateUserRole(userId, role);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/exists/{userId}")
    public ResponseEntity<Boolean> checkUserExists(@PathVariable Long userId) {
        boolean userExists = userService.userExists(userId);
        return ResponseEntity.ok(userExists);
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<String> changePassword(@PathVariable Long userId,
                                                 @RequestBody ChangePasswordRequest request,
                                                 @RequestHeader("Authorization") String token) {
        if (!isAuthorized(token, userId)) {
            return ResponseEntity.status(403).body("Unauthorized to change this password");
        }

        boolean success = userService.changePassword(userId, request.getOldPassword(), request.getNewPassword());

        if (success) {
            return ResponseEntity.ok("Password changed successfully");
        } else {
            return ResponseEntity.badRequest().body("Old password is incorrect");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam(required = false) String name,
                                                  @RequestParam(required = false) String username,
                                                  @RequestParam(required = false) String email) {
        List<User> users = userService.searchUsers(name, username, email);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}/email")
    public ResponseEntity<String> getUserEmail(@PathVariable Long userId) {
        Optional<User> user = userService.getUserById(userId);
        return user.map(u -> ResponseEntity.ok(u.getEmail()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


}
