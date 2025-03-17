package com.app.community.business.service;

import com.app.community.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    Event createEvent(Event event);
    List<Event> getAllEvents(Long groupId, LocalDateTime startDate, LocalDateTime endDate);
    Event getEventById(Long eventId);
    Event updateEvent(Long eventId, Event event);
    void deleteEvent(Long eventId);
}
