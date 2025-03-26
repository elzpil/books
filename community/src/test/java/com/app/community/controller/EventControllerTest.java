package com.app.community.controller;

import com.app.community.business.service.EventService;
import com.app.community.dto.EventUpdateDTO;
import com.app.community.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EventControllerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    private Event event;
    private EventUpdateDTO eventUpdateDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        event = new Event();
        event.setEventId(1L);
        event.setGroupId(1L);
        event.setUserId(100L);
        event.setName("Test Event");
        event.setDescription("Test description for the event");
        event.setEventDate(LocalDateTime.now().plusDays(1));
        event.setCreatedAt(LocalDateTime.now());

        eventUpdateDTO = new EventUpdateDTO();
        eventUpdateDTO.setName("Updated Event");
        eventUpdateDTO.setDescription("Updated description for the event");
        eventUpdateDTO.setEventDate(LocalDateTime.now().plusDays(2));
    }

    @Test
    void testCreateEvent() {
        String token = "Bearer token";

        when(eventService.createEvent(any(Event.class), eq(token))).thenReturn(event);

        ResponseEntity<Event> response = eventController.createEvent(event, token);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(event, response.getBody());
        verify(eventService, times(1)).createEvent(any(Event.class), eq(token));
    }

    @Test
    void testGetAllEvents() {
        Long groupId = 1L;
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(7);

        Event event1 = new Event();
        event1.setEventId(1L);
        event1.setName("Event 1");

        Event event2 = new Event();
        event2.setEventId(2L);
        event2.setName("Event 2");

        when(eventService.getAllEvents(eq(groupId), eq(startDate), eq(endDate)))
                .thenReturn(List.of(event1, event2));

        ResponseEntity<List<Event>> response = eventController.getAllEvents(groupId, startDate, endDate);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(eventService, times(1)).getAllEvents(eq(groupId), eq(startDate), eq(endDate));
    }

    @Test
    void testGetEventById() {
        Long eventId = 1L;

        when(eventService.getEventById(eventId)).thenReturn(event);

        ResponseEntity<Event> response = eventController.getEventById(eventId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(event, response.getBody());
        verify(eventService, times(1)).getEventById(eventId);
    }

    @Test
    void testUpdateEvent() {
        Long eventId = 1L;
        String token = "Bearer token";

        when(eventService.updateEvent(eq(eventId), any(EventUpdateDTO.class), eq(token))).thenReturn(event);

        ResponseEntity<Event> response = eventController.updateEvent(eventId, eventUpdateDTO, token);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(event, response.getBody());
        verify(eventService, times(1)).updateEvent(eq(eventId), any(EventUpdateDTO.class), eq(token));
    }

    @Test
    void testDeleteEvent() {
        Long eventId = 1L;
        String token = "Bearer token";

        doNothing().when(eventService).deleteEvent(eventId, token);

        ResponseEntity<Void> response = eventController.deleteEvent(eventId, token);

        assertEquals(204, response.getStatusCodeValue());
        verify(eventService, times(1)).deleteEvent(eventId, token);
    }
}
