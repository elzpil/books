package com.app.community.controller;

import com.app.community.business.service.EventService;
import com.app.community.dto.EventUpdateDTO;
import com.app.community.model.Event;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event,
                                             @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(eventService.createEvent(event, token));
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents(
            @RequestParam(required = false) Long groupId,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        log.info("Getting all events for group: {}", groupId);
        return ResponseEntity.ok(eventService.getAllEvents(groupId, startDate, endDate));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<Event> getEventById(@PathVariable Long eventId) {
        Event event = eventService.getEventById(eventId);
        log.info("Getting event with id: {}", eventId);
        return event != null ? ResponseEntity.ok(event) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long eventId, @Valid @RequestBody EventUpdateDTO eventUpdateDTO,
                                             @RequestHeader("Authorization") String token) {

        log.info("Updating event with id: {}", eventId);
        Event updatedEvent = eventService.updateEvent(eventId, eventUpdateDTO, token);
        return updatedEvent != null ? ResponseEntity.ok(updatedEvent) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId, @RequestHeader("Authorization") String token) {
        eventService.deleteEvent(eventId, token);
        log.info("Deleting event with id: {}", eventId);
        return ResponseEntity.noContent().build();
    }
}
