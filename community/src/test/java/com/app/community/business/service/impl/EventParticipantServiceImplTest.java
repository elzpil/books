package com.app.community.business.service.impl;

import com.app.community.auth.util.JwtTokenUtil;
import com.app.community.business.mapper.EventParticipantMapper;
import com.app.community.business.repository.EventParticipantRepository;
import com.app.community.business.repository.model.EventParticipantDAO;
import com.app.community.dto.EventParticipantUpdateDTO;
import com.app.community.model.EventParticipant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventParticipantServiceImplTest {

    @Mock
    private EventParticipantRepository eventParticipantRepository;

    @Mock
    private EventParticipantMapper eventParticipantMapper;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private EventParticipantServiceImpl eventParticipantService;

    private EventParticipant eventParticipant;
    private EventParticipantDAO eventParticipantDAO;
    private final String token = "Bearer sample.token.here";
    private final Long userId = 1L;
    private final Long eventId = 100L;

    @BeforeEach
    void setUp() {
        eventParticipant = new EventParticipant();
        eventParticipant.setUserId(userId);
        eventParticipant.setEventId(eventId);
        eventParticipant.setRsvpStatus("Going");
        eventParticipant.setCreatedAt(LocalDateTime.now());

        eventParticipantDAO = new EventParticipantDAO();
        eventParticipantDAO.setUserId(userId);
        eventParticipantDAO.setEventId(eventId);
        eventParticipantDAO.setRsvpStatus("Going");
        eventParticipantDAO.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void rsvpToEvent_Success() {
        when(jwtTokenUtil.extractUserId(token.replace("Bearer ", ""))).thenReturn(userId);
        when(eventParticipantRepository.existsByUserIdAndEventId(userId, eventId)).thenReturn(false);
        when(eventParticipantMapper.eventParticipantToEventParticipantDAO(eventParticipant)).thenReturn(eventParticipantDAO);
        when(eventParticipantRepository.save(eventParticipantDAO)).thenReturn(eventParticipantDAO);
        when(eventParticipantMapper.eventParticipantDAOToEventParticipant(eventParticipantDAO)).thenReturn(eventParticipant);

        EventParticipant result = eventParticipantService.rsvpToEvent(eventId, eventParticipant, token);

        assertNotNull(result);
        assertEquals(eventId, result.getEventId());
        assertEquals(userId, result.getUserId());
        assertEquals("Going", result.getRsvpStatus());
        verify(eventParticipantRepository).save(eventParticipantDAO);
    }

    @Test
    void rsvpToEvent_AlreadyRSVPd_ThrowsException() {
        when(jwtTokenUtil.extractUserId(token.replace("Bearer ", ""))).thenReturn(userId);
        when(eventParticipantRepository.existsByUserIdAndEventId(userId, eventId)).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                eventParticipantService.rsvpToEvent(eventId, eventParticipant, token));

        assertEquals("User has already RSVP'd to this event.", exception.getMessage());
        verify(eventParticipantRepository, never()).save(any());
    }

    @Test
    void getParticipantsForEvent_Success() {
        when(eventParticipantRepository.findByEventId(eventId)).thenReturn(List.of(eventParticipantDAO));
        when(eventParticipantMapper.eventParticipantDAOToEventParticipant(eventParticipantDAO)).thenReturn(eventParticipant);

        List<EventParticipant> result = eventParticipantService.getParticipantsForEvent(eventId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(eventId, result.get(0).getEventId());
        verify(eventParticipantRepository).findByEventId(eventId);
    }

    @Test
    void getParticipantsForEventByRsvpStatus_Success() {
        String status = "Going";
        when(eventParticipantRepository.findByEventIdAndRsvpStatus(eventId, status)).thenReturn(List.of(eventParticipantDAO));
        when(eventParticipantMapper.eventParticipantDAOToEventParticipant(eventParticipantDAO)).thenReturn(eventParticipant);

        List<EventParticipant> result = eventParticipantService.getParticipantsForEventByRsvpStatus(eventId, status);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Going", result.get(0).getRsvpStatus());
        verify(eventParticipantRepository).findByEventIdAndRsvpStatus(eventId, status);
    }


    @Test
    void updateRsvpStatus_NotFound_ThrowsException() {
        EventParticipantUpdateDTO updateDTO = new EventParticipantUpdateDTO();
        updateDTO.setRsvpStatus("Maybe");

        when(jwtTokenUtil.extractUserId(token.replace("Bearer ", ""))).thenReturn(userId);
        when(eventParticipantRepository.findByUserIdAndEventId(userId, eventId)).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                eventParticipantService.updateRsvpStatus(eventId, updateDTO, token));

        assertEquals("RSVP not found for this event", exception.getMessage());
        verify(eventParticipantRepository, never()).save(any());
    }
}
