package com.app.community.controller;

import com.app.community.business.service.EventParticipantService;
import com.app.community.business.service.impl.UserServiceClient;
import com.app.community.dto.EventParticipantUpdateDTO;
import com.app.community.model.EventParticipant;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events/{eventId}/participants")
public class EventParticipantController {

    private final EventParticipantService eventParticipantService;
    private final UserServiceClient userServiceClient;

    public EventParticipantController(EventParticipantService eventParticipantService, UserServiceClient userServiceClient) {
        this.eventParticipantService = eventParticipantService;
        this.userServiceClient = userServiceClient;
    }

    @PostMapping("/rsvp")
    public ResponseEntity<EventParticipant> rsvpToEvent(@PathVariable  Long eventId,
                                                        @RequestBody EventParticipant eventParticipant,
                                                        @RequestHeader("Authorization") String token) {

        EventParticipant savedParticipant = eventParticipantService.rsvpToEvent(eventId, eventParticipant, token);
        log.info("RSVP to event with id: {}", eventParticipant.getEventId());
        return ResponseEntity.ok(savedParticipant);
    }


    @GetMapping
    public ResponseEntity<List<EventParticipant>> getParticipants(
            @PathVariable Long eventId,
            @RequestParam(required = false) String rsvpStatus) {
        List<EventParticipant> participants;

        if (rsvpStatus != null) {
            // Call service method with rsvpStatus filter
            log.info("Getting participants for event with id: {} with rsvp status", eventId);
            participants = eventParticipantService.getParticipantsForEventByRsvpStatus(eventId, rsvpStatus);
        } else {
            // Call service method to get all participants for the event
            log.info("Getting participants for event with id: {}", eventId);
            participants = eventParticipantService.getParticipantsForEvent(eventId);
        }
        return ResponseEntity.ok(participants);
    }

    @PutMapping("/rsvp")
    public ResponseEntity<EventParticipant> updateRsvpStatus(
            @PathVariable Long eventId,
            @RequestBody @Valid EventParticipantUpdateDTO updateDTO,
            @RequestHeader("Authorization") String token) {

        EventParticipant updatedParticipant = eventParticipantService.updateRsvpStatus(eventId, updateDTO, token);
        log.info("Updated RSVP for event {}: {}", eventId, updateDTO.getRsvpStatus());

        return ResponseEntity.ok(updatedParticipant);
    }

}
