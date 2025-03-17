package com.app.community.business.service.impl;

import com.app.community.business.mapper.EventParticipantMapper;
import com.app.community.business.repository.EventParticipantRepository;
import com.app.community.business.repository.model.EventParticipantDAO;
import com.app.community.business.service.EventParticipantService;
import com.app.community.model.EventParticipant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EventParticipantServiceImpl implements EventParticipantService {

    private final EventParticipantRepository eventParticipantRepository;
    private final EventParticipantMapper eventParticipantMapper;
    private final UserServiceClient userServiceClient;

    public EventParticipantServiceImpl(
            EventParticipantRepository eventParticipantRepository,
            EventParticipantMapper eventParticipantMapper,
            UserServiceClient userServiceClient) {
        this.eventParticipantRepository = eventParticipantRepository;
        this.eventParticipantMapper = eventParticipantMapper;
        this.userServiceClient = userServiceClient;
    }

    @Override
    public EventParticipant rsvpToEvent(EventParticipant eventParticipant) {
        if (!userServiceClient.doesUserExist(eventParticipant.getUserId())) {
            throw new IllegalArgumentException("User with ID " + eventParticipant.getUserId() + " does not exist");
        }

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
}
