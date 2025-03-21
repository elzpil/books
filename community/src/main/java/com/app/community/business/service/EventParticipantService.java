package com.app.community.business.service;

import com.app.community.dto.EventParticipantUpdateDTO;
import com.app.community.model.EventParticipant;

import java.util.List;

public interface EventParticipantService {

    EventParticipant rsvpToEvent(Long eventId, EventParticipant eventParticipant, String token);

    List<EventParticipant> getParticipantsForEvent(Long eventId);

    List<EventParticipant> getParticipantsForEventByRsvpStatus(Long eventId, String rsvpStatus);
    EventParticipant updateRsvpStatus(Long eventId, EventParticipantUpdateDTO updateDTO, String token);

}
