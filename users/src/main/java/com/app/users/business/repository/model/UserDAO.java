package com.app.users.business.repository.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class UserDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true, name = "email", columnDefinition = "VARCHAR")
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(length = 100, name = "name", columnDefinition = "VARCHAR")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "user_role", nullable = false)
    private String role;

    @Column(name = "username", nullable = false, columnDefinition = "VARCHAR")
    private String username;

    public UserDAO() {
        this.createdAt = LocalDateTime.now();
    }
    public UserDAO(Long id) {
        this.id = id;
        this.createdAt = LocalDateTime.now();
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
