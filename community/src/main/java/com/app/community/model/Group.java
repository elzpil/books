package com.app.community.model;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class Group {

    private Long id;
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
    private PrivacySetting privacySetting;
    @NotBlank(message = "Creator id is required")
    private Long creatorId;
    @FutureOrPresent(message = "Created at date cannot be in the past")
    private LocalDateTime createdAt;

    public Group() {}

    public Group(Long id, String name, String description, PrivacySetting privacySetting, Long creatorId, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.privacySetting = privacySetting;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PrivacySetting getPrivacySetting() {
        return privacySetting;
    }

    public void setPrivacySetting(PrivacySetting privacySetting) {
        this.privacySetting = privacySetting;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
