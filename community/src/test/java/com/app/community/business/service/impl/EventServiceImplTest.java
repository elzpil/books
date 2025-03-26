package com.app.community.business.service.impl;

import com.app.community.auth.util.JwtTokenUtil;
import com.app.community.business.mapper.EventMapper;
import com.app.community.business.repository.EventRepository;
import com.app.community.business.repository.model.EventDAO;
import com.app.community.business.service.EventService;
import com.app.community.dto.EventUpdateDTO;
import com.app.community.exception.ResourceNotFoundException;
import com.app.community.exception.UnauthorizedException;
import com.app.community.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event event;
    private EventDAO eventDAO;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setEventId(1L);
        event.setGroupId(1L);
        event.setUserId(100L);
        event.setName("Test Event");
        event.setDescription("Event Description");
        event.setEventDate(LocalDateTime.now().plusDays(1));
        event.setCreatedAt(LocalDateTime.now());

        eventDAO = new EventDAO();
        eventDAO.setEventId(1L);
        eventDAO.setGroupId(1L);
        eventDAO.setUserId(100L);
        eventDAO.setName("Test Event");
        eventDAO.setDescription("Event Description");
        eventDAO.setEventDate(LocalDateTime.now().plusDays(1));
        eventDAO.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateEvent_Success() {
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(100L);
        when(eventMapper.eventToEventDAO(any(Event.class))).thenReturn(eventDAO);
        when(eventRepository.save(any(EventDAO.class))).thenReturn(eventDAO);
        when(eventMapper.eventDAOToEvent(any(EventDAO.class))).thenReturn(event);

        Event result = eventService.createEvent(event, "Bearer token");

        assertNotNull(result);
        assertEquals(event.getEventId(), result.getEventId());
        assertEquals(event.getName(), result.getName());
        verify(eventRepository, times(1)).save(any(EventDAO.class));
    }

    @Test
    void testGetAllEvents_Success() {
        when(eventRepository.findByGroupId(anyLong())).thenReturn(List.of(eventDAO));
        when(eventMapper.eventDAOToEvent(any(EventDAO.class))).thenReturn(event);

        List<Event> events = eventService.getAllEvents(1L, null, null);

        assertFalse(events.isEmpty());
        assertEquals(1, events.size());
        verify(eventRepository, times(1)).findByGroupId(anyLong());
    }

    @Test
    void testGetEventById_Success() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(eventDAO));
        when(eventMapper.eventDAOToEvent(any(EventDAO.class))).thenReturn(event);

        Event result = eventService.getEventById(1L);

        assertNotNull(result);
        assertEquals(event.getEventId(), result.getEventId());
        verify(eventRepository, times(1)).findById(anyLong());
    }

    @Test
    void testUpdateEvent_Success() {
        EventUpdateDTO updateDTO = new EventUpdateDTO();
        updateDTO.setName("Updated Event");

        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(eventDAO));
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(100L);
        when(eventRepository.save(any(EventDAO.class))).thenReturn(eventDAO);
        when(eventMapper.eventDAOToEvent(any(EventDAO.class))).thenReturn(event);

        Event result = eventService.updateEvent(1L, updateDTO, "Bearer token");

        assertNotNull(result);
        assertEquals("Updated Event", eventDAO.getName());
        verify(eventRepository, times(1)).save(any(EventDAO.class));
    }

    @Test
    void testUpdateEvent_Unauthorized() {
        EventUpdateDTO updateDTO = new EventUpdateDTO();
        updateDTO.setName("Updated Event");

        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(eventDAO));
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(200L);

        assertThrows(UnauthorizedException.class, () -> eventService.updateEvent(1L, updateDTO, "Bearer token"));
        verify(eventRepository, never()).save(any(EventDAO.class));
    }

    @Test
    void testDeleteEvent_Success() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(eventDAO));
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(100L);

        eventService.deleteEvent(1L, "Bearer token");

        verify(eventRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteEvent_Unauthorized() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(eventDAO));
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(200L);

        assertThrows(UnauthorizedException.class, () -> eventService.deleteEvent(1L, "Bearer token"));
        verify(eventRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteEvent_NotFound() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.deleteEvent(1L, "Bearer token"));
        verify(eventRepository, never()).deleteById(anyLong());
    }
}
