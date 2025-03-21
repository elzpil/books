package com.app.community.business.service.impl;

import com.app.community.auth.util.JwtTokenUtil;
import com.app.community.business.mapper.EventMapper;
import com.app.community.business.repository.EventRepository;
import com.app.community.business.repository.model.EventDAO;
import com.app.community.business.repository.model.EventDAO;
import com.app.community.business.service.EventService;
import com.app.community.dto.EventUpdateDTO;
import com.app.community.exception.ResourceNotFoundException;
import com.app.community.exception.UnauthorizedException;
import com.app.community.model.Event;
import com.app.community.model.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final JwtTokenUtil jwtTokenUtil;

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper,
                            JwtTokenUtil jwtTokenUtil) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public Event createEvent(Event event, String token) {
        Long userId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));
        log.info("Validating user with ID: {}", userId);

        event.setCreatedAt(LocalDateTime.now());
        event.setUserId(userId);
        EventDAO discussionDAO = eventRepository.save(eventMapper.eventToEventDAO(event));

        log.info("Saving new event: {}", discussionDAO);
        return eventMapper.eventDAOToEvent(discussionDAO);
    }

    @Override
    public List<Event> getAllEvents(Long groupId, LocalDateTime startDate, LocalDateTime endDate) {
        List<EventDAO> events;
        log.info("Getting all events");
        if (groupId != null && startDate != null && endDate != null) {
            events = eventRepository.findByGroupIdAndEventDateBetween(groupId, startDate, endDate);
        } else if (groupId != null) {
            events = eventRepository.findByGroupId(groupId);
        } else {
            events = eventRepository.findAll();
        }
        return events.stream().map(eventMapper::eventDAOToEvent).toList();
    }

    @Override
    public Event getEventById(Long eventId) {
        log.info("Getting event by id: {}", eventId);
        return eventRepository.findById(eventId)
                .map(eventMapper::eventDAOToEvent)
                .orElse(null);
    }

    @Transactional
    @Override
    public Event updateEvent(Long eventId, EventUpdateDTO eventUpdateDTO, String token) {

        EventDAO existingEvent= eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", eventId));

        // Check if the user is authorized
        if (!isAuthorized(token, existingEvent.getUserId())) {
            log.warn("Unauthorized attempt to delete event ID {} by user ID {}", eventId, existingEvent.getUserId());
            throw new UnauthorizedException("You are not authorized to delete this event");
        }

        if (eventUpdateDTO.getName() != null) {
            existingEvent.setName(eventUpdateDTO.getName());
        }
        if (eventUpdateDTO.getDescription() != null) {
            existingEvent.setDescription(eventUpdateDTO.getDescription());
        }
        if (eventUpdateDTO.getEventDate() != null) {
            existingEvent.setEventDate(eventUpdateDTO.getEventDate());
        }

        EventDAO updatedEvent = eventRepository.save(existingEvent);
        log.info("Updating event: {}", existingEvent);
        return eventMapper.eventDAOToEvent(updatedEvent);
    }

    @Override
    public void deleteEvent(Long eventId, String token) {
        EventDAO existingEvent= eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", eventId));

        // Check if the user is authorized
        if (!isAuthorized(token, existingEvent.getUserId())) {
            log.warn("Unauthorized attempt to delete event ID {} by user ID {}", eventId, existingEvent.getUserId());
            throw new UnauthorizedException("You are not authorized to delete this event");
        }
        log.info("Deleting event with id: {}", eventId);
        eventRepository.deleteById(eventId);
    }

    private boolean isAuthorized(String token, Long userId) {
        Long tokenUserId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));
        String role = jwtTokenUtil.extractRole(token.replace("Bearer ", ""));
        return tokenUserId.equals(userId) || "ADMIN".equals(role);
    }
}
