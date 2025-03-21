package com.app.community.dto;

import jakarta.validation.constraints.NotBlank;

public class EventParticipantUpdateDTO {
    @NotBlank(message = "RSVP status is required")
    private String rsvpStatus;

    public String getRsvpStatus() {
        return rsvpStatus;
    }

    public void setRsvpStatus(String rsvpStatus) {
        this.rsvpStatus = rsvpStatus;
    }
}
