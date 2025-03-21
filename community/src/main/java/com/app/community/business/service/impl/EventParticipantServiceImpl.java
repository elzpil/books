package com.app.community.business.service.impl;

import com.app.community.auth.util.JwtTokenUtil;
import com.app.community.business.mapper.EventParticipantMapper;
import com.app.community.business.repository.EventParticipantRepository;
import com.app.community.business.repository.model.EventParticipantDAO;
import com.app.community.business.service.EventParticipantService;
import com.app.community.dto.EventParticipantUpdateDTO;
import com.app.community.model.EventParticipant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EventParticipantServiceImpl implements EventParticipantService {

    private final EventParticipantRepository eventParticipantRepository;
    private final EventParticipantMapper eventParticipantMapper;
    private JwtTokenUtil jwtTokenUtil;

    public EventParticipantServiceImpl(
            EventParticipantRepository eventParticipantRepository,
            EventParticipantMapper eventParticipantMapper,
            JwtTokenUtil jwtTokenUtil) {
        this.eventParticipantRepository = eventParticipantRepository;
        this.eventParticipantMapper = eventParticipantMapper;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public EventParticipant rsvpToEvent(Long eventId, EventParticipant eventParticipant, String token) {
        Long userId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));

        boolean exists = eventParticipantRepository.existsByUserIdAndEventId(userId, eventId);
        if (exists) {
            throw new IllegalStateException("User has already RSVP'd to this event.");
        }
        eventParticipant.setUserId(userId);
        eventParticipant.setEventId(eventId);
        eventParticipant.setCreatedAt(LocalDateTime.now());
        EventParticipantDAO participantDAO = eventParticipantMapper.eventParticipantToEventParticipantDAO(eventParticipant);
        EventParticipantDAO savedDAO = eventParticipantRepository.save(participantDAO);
        log.info("RSVP to event: {}", savedDAO);
        return eventParticipantMapper.eventParticipantDAOToEventParticipant(savedDAO);
    }

    @Override
    public List<EventParticipant> getParticipantsForEvent(Long eventId) {
        List<EventParticipantDAO> participantDAOs = eventParticipantRepository.findByEventId(eventId);
        log.info("Getting participants");
        return participantDAOs.stream()
                .map(eventParticipantMapper::eventParticipantDAOToEventParticipant)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventParticipant> getParticipantsForEventByRsvpStatus(Long eventId, String rsvpStatus) {
        log.info("Getting participants by rsvp status");
        List<EventParticipantDAO> participantDAOs = eventParticipantRepository.findByEventIdAndRsvpStatus(eventId, rsvpStatus);
        return participantDAOs.stream()
                .map(eventParticipantMapper::eventParticipantDAOToEventParticipant)
                .collect(Collectors.toList());
    }

    @Override
    public EventParticipant updateRsvpStatus(Long eventId, EventParticipantUpdateDTO updateDTO, String token) {
        Long userId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));

        EventParticipantDAO participantDAO = eventParticipantRepository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new IllegalStateException("RSVP not found for this event"));

        participantDAO.setRsvpStatus(updateDTO.getRsvpStatus());
        EventParticipantDAO updatedDAO = eventParticipantRepository.save(participantDAO);

        log.info("Updated RSVP status for user {} in event {}: {}", userId, eventId, updatedDAO.getRsvpStatus());
        return eventParticipantMapper.eventParticipantDAOToEventParticipant(updatedDAO);
    }

}
