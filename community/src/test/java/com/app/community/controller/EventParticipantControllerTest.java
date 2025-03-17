package com.app.community.controller;

import com.app.community.business.service.EventParticipantService;
import com.app.community.model.EventParticipant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventParticipantControllerTest {

    @Mock
    private EventParticipantService eventParticipantService;

    @InjectMocks
    private EventParticipantController eventParticipantController;

    private EventParticipant eventParticipant;

    @BeforeEach
    void setUp() {
        eventParticipant = new EventParticipant();
        eventParticipant.setParticipantId(1L);
        eventParticipant.setEventId(5L);
        eventParticipant.setUserId(100L);
        eventParticipant.setRsvpStatus("Going");
        eventParticipant.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void rsvpToEvent_ShouldReturnEventParticipant() {
        when(eventParticipantService.rsvpToEvent(5L, 100L, "Going")).thenReturn(eventParticipant);

        ResponseEntity<EventParticipant> response = eventParticipantController.rsvpToEvent(5L, 100L, "Going");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(eventParticipant);
        verify(eventParticipantService).rsvpToEvent(5L, 100L, "Going");
    }

    @Test
    void getParticipants_ShouldReturnListOfParticipants() {
        when(eventParticipantService.getParticipantsForEvent(5L)).thenReturn(List.of(eventParticipant));

        ResponseEntity<List<EventParticipant>> response = eventParticipantController.getParticipants(5L, null);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull().hasSize(1);
        verify(eventParticipantService).getParticipantsForEvent(5L);
    }

    @Test
    void getParticipants_WithRsvpStatus_ShouldReturnFilteredParticipants() {
        when(eventParticipantService.getParticipantsForEventByRsvpStatus(5L, "Going"))
                .thenReturn(List.of(eventParticipant));

        ResponseEntity<List<EventParticipant>> response = eventParticipantController.getParticipants(5L, "Going");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull().hasSize(1);
        verify(eventParticipantService).getParticipantsForEventByRsvpStatus(5L, "Going");
    }
}
