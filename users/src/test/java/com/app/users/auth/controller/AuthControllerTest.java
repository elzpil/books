package com.app.users.auth.controller;

import com.app.users.auth.util.JwtTokenUtil;
import com.app.users.business.service.UserService;
import com.app.users.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRole("USER");
    }

    @Test
    void testRegister_Success() {
        when(userService.existsByUsername(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");

        ResponseEntity<String> response = authController.register(user);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User registered successfully", response.getBody());
        verify(userService).saveUser(any(User.class));
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        when(userService.existsByUsername(user.getEmail())).thenReturn(true);

        ResponseEntity<String> response = authController.register(user);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Email already exists", response.getBody());
        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    void testLogin_Success() {
        when(userService.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(user.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtTokenUtil.generateToken(user.getEmail(), user.getId(), user.getRole())).thenReturn("jwtToken");

        ResponseEntity<Map<String, String>> response = authController.login(user);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("jwtToken", response.getBody().get("token"));
    }

    @Test
    void testLogin_UserNotFound() {
        when(userService.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        ResponseEntity<Map<String, String>> response = authController.login(user);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid credentials", response.getBody().get("message"));
    }

    @Test
    void testLogin_InvalidPassword() {
        when(userService.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        ResponseEntity<Map<String, String>> response = authController.login(user);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid credentials", response.getBody().get("message"));
    }

    @Test
    void testLogout() {
        ResponseEntity<String> response = authController.logout();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User logged out successfully", response.getBody());
    }

    @Test
    void testRefresh_Success() {
        String oldToken = "Bearer validToken";
        when(jwtTokenUtil.isTokenExpired("validToken")).thenReturn(false);
        when(jwtTokenUtil.extractUsername("validToken")).thenReturn(user.getEmail());
        when(jwtTokenUtil.extractUserId("validToken")).thenReturn(user.getId());
        when(jwtTokenUtil.extractRole("validToken")).thenReturn(user.getRole());
        when(jwtTokenUtil.generateToken(user.getEmail(), user.getId(), user.getRole())).thenReturn("newJwtToken");

        ResponseEntity<Map<String, String>> response = authController.refresh(oldToken);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("newJwtToken", response.getBody().get("token"));
    }

    @Test
    void testRefresh_TokenExpired() {
        String oldToken = "Bearer expiredToken";
        when(jwtTokenUtil.isTokenExpired("expiredToken")).thenReturn(true);

        ResponseEntity<Map<String, String>> response = authController.refresh(oldToken);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Token expired", response.getBody().get("message"));
    }
}
