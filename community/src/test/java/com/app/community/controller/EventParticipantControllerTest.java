package com.app.community.controller;

import com.app.community.business.service.EventParticipantService;
import com.app.community.dto.EventParticipantUpdateDTO;
import com.app.community.model.EventParticipant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EventParticipantControllerTest {

    @Mock
    private EventParticipantService eventParticipantService;

    @InjectMocks
    private EventParticipantController eventParticipantController;

    private EventParticipant eventParticipant;
    private EventParticipantUpdateDTO eventParticipantUpdateDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        eventParticipant = new EventParticipant();
        eventParticipant.setParticipantId(1L);
        eventParticipant.setUserId(100L);
        eventParticipant.setEventId(1L);
        eventParticipant.setRsvpStatus("Going");
        eventParticipant.setCreatedAt(LocalDateTime.now());

        eventParticipantUpdateDTO = new EventParticipantUpdateDTO();
        eventParticipantUpdateDTO.setRsvpStatus("Not Going");
    }

    @Test
    void testRsvpToEvent() {
        String token = "Bearer token";
        when(eventParticipantService.rsvpToEvent(eq(1L), any(EventParticipant.class), eq(token))).thenReturn(eventParticipant);

        ResponseEntity<EventParticipant> response = eventParticipantController.rsvpToEvent(1L, eventParticipant, token);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(eventParticipant, response.getBody());
        verify(eventParticipantService, times(1)).rsvpToEvent(eq(1L), any(EventParticipant.class), eq(token));
    }

    @Test
    void testGetParticipants() {
        List<EventParticipant> participants = List.of(eventParticipant);
        when(eventParticipantService.getParticipantsForEvent(eq(1L))).thenReturn(participants);

        ResponseEntity<List<EventParticipant>> response = eventParticipantController.getParticipants(1L, null);

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        assertEquals(eventParticipant, response.getBody().get(0));
        verify(eventParticipantService, times(1)).getParticipantsForEvent(eq(1L));
    }

    @Test
    void testGetParticipantsWithRsvpStatus() {
        List<EventParticipant> participants = List.of(eventParticipant);
        String rsvpStatus = "Going";
        when(eventParticipantService.getParticipantsForEventByRsvpStatus(eq(1L), eq(rsvpStatus)))
                .thenReturn(participants);

        ResponseEntity<List<EventParticipant>> response = eventParticipantController.getParticipants(1L, rsvpStatus);

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        assertEquals(eventParticipant, response.getBody().get(0));
        verify(eventParticipantService, times(1)).getParticipantsForEventByRsvpStatus(eq(1L), eq(rsvpStatus));
    }

    @Test
    void testUpdateRsvpStatus() {
        String token = "Bearer token";
        eventParticipant.setRsvpStatus("Not Going");
        when(eventParticipantService.updateRsvpStatus(eq(1L), any(EventParticipantUpdateDTO.class), eq(token)))
                .thenReturn(eventParticipant);

        ResponseEntity<EventParticipant> response = eventParticipantController.updateRsvpStatus(1L, eventParticipantUpdateDTO, token);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Not Going", response.getBody().getRsvpStatus());
        verify(eventParticipantService, times(1)).updateRsvpStatus(eq(1L), any(EventParticipantUpdateDTO.class), eq(token));
    }
}
