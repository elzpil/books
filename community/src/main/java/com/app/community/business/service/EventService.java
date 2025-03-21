package com.app.community.business.service;

import com.app.community.dto.EventUpdateDTO;
import com.app.community.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    Event createEvent(Event event, String token);
    List<Event> getAllEvents(Long groupId, LocalDateTime startDate, LocalDateTime endDate);
    Event getEventById(Long eventId);
    Event updateEvent(Long eventId, EventUpdateDTO eventUpdateDTO, String token);
    void deleteEvent(Long eventId,  String token);
}
