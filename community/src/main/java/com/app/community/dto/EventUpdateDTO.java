package com.app.community.dto;

import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDateTime;

public class EventUpdateDTO {

    private String name;

    private String description;
    @FutureOrPresent(message = "Event date cannot be in the past")
    private LocalDateTime eventDate;

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

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }
}
