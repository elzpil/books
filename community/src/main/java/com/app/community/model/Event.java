package com.app.community.model;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class Event {
    private Long eventId;
    @NotBlank(message = "Group id is required")
    private Long groupId;
    private Long userId; //creator
    @NotBlank(message = "Event name is required")
    private String name;
    @NotBlank(message = "User id is required")
    private String description;
    @FutureOrPresent(message = "Event date cannot be in the past")
    private LocalDateTime eventDate;
    @FutureOrPresent(message = "Created at date cannot be in the past")
    private LocalDateTime createdAt;

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
