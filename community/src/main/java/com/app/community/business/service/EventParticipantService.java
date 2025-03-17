package com.app.community.business.service;

import com.app.community.model.EventParticipant;

import java.util.List;

public interface EventParticipantService {

    EventParticipant rsvpToEvent(EventParticipant eventParticipant);

    List<EventParticipant> getParticipantsForEvent(Long eventId);

    List<EventParticipant> getParticipantsForEventByRsvpStatus(Long eventId, String rsvpStatus);
}
