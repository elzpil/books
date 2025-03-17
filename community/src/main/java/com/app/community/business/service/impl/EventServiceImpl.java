package com.app.community.business.service.impl;

import com.app.community.business.mapper.EventMapper;
import com.app.community.business.repository.EventRepository;
import com.app.community.business.repository.model.EventDAO;
import com.app.community.business.service.EventService;
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

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    public Event createEvent(Event event) {
        EventDAO eventDAO = eventMapper.eventToEventDAO(event);
        eventDAO.setCreatedAt(LocalDateTime.now());
        EventDAO savedEvent = eventRepository.save(eventDAO);
        log.info("Creating event: {}", eventDAO);
        return eventMapper.eventDAOToEvent(savedEvent);
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
    public Event updateEvent(Long eventId, Event event) {
        EventDAO eventDAO = eventRepository.findById(eventId).orElse(null);
        if (eventDAO != null) {
            eventDAO.setName(event.getName());
            eventDAO.setDescription(event.getDescription());
            eventDAO.setEventDate(event.getEventDate());
            EventDAO updatedEvent = eventRepository.save(eventDAO);
            log.info("Updating event: {}", eventDAO);
            return eventMapper.eventDAOToEvent(updatedEvent);
        }
        return null;
    }

    @Override
    public void deleteEvent(Long eventId) {
        log.info("Deleting event with id: {}", eventId);
        eventRepository.deleteById(eventId);
    }
}
