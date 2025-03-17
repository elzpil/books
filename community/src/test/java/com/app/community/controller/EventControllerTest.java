package com.app.community.controller;

import com.app.community.business.service.EventService;
import com.app.community.model.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateEvent() throws Exception {
        Event event = new Event();
        event.setEventId(1L);
        event.setGroupId(2L);
        event.setName("Book Club Meeting");
        event.setDescription("Discussing the latest book");
        event.setEventDate(LocalDateTime.now().plusDays(5));
        event.setCreatedAt(LocalDateTime.now());

        when(eventService.createEvent(any(Event.class))).thenReturn(event);

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(1L))
                .andExpect(jsonPath("$.name").value("Book Club Meeting"));
    }

    @Test
    void testGetAllEvents() throws Exception {
        Event event1 = new Event();
        event1.setEventId(1L);
        event1.setGroupId(2L);
        event1.setName("Event One");
        event1.setDescription("Description One");
        event1.setEventDate(LocalDateTime.now().plusDays(5));
        event1.setCreatedAt(LocalDateTime.now());

        Event event2 = new Event();
        event2.setEventId(2L);
        event2.setGroupId(2L);
        event2.setName("Event Two");
        event2.setDescription("Description Two");
        event2.setEventDate(LocalDateTime.now().plusDays(10));
        event2.setCreatedAt(LocalDateTime.now());

        List<Event> eventList = Arrays.asList(event1, event2);
        when(eventService.getAllEvents(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(eventList);

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Event One"))
                .andExpect(jsonPath("$[1].name").value("Event Two"));
    }

    @Test
    void testGetEventById() throws Exception {
        Event event = new Event();
        event.setEventId(1L);
        event.setGroupId(2L);
        event.setName("Event Test");
        event.setDescription("Test Description");
        event.setEventDate(LocalDateTime.now().plusDays(5));
        event.setCreatedAt(LocalDateTime.now());

        when(eventService.getEventById(1L)).thenReturn(event);

        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(1L))
                .andExpect(jsonPath("$.name").value("Event Test"));
    }

    @Test
    void testUpdateEvent() throws Exception {
        Event updatedEvent = new Event();
        updatedEvent.setEventId(1L);
        updatedEvent.setGroupId(2L);
        updatedEvent.setName("Updated Event");
        updatedEvent.setDescription("Updated Description");
        updatedEvent.setEventDate(LocalDateTime.now().plusDays(5));
        updatedEvent.setCreatedAt(LocalDateTime.now());

        when(eventService.updateEvent(Mockito.eq(1L), any(Event.class))).thenReturn(updatedEvent);

        mockMvc.perform(put("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEvent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Event"));
    }

    @Test
    void testDeleteEvent() throws Exception {
        mockMvc.perform(delete("/events/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(eventService).deleteEvent(1L);
    }
}
