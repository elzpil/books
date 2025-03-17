package com.app.community.business.repository;

import com.app.community.business.repository.model.EventParticipantDAO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventParticipantRepository extends JpaRepository<EventParticipantDAO, Long> {

    List<EventParticipantDAO> findByEventId(Long eventId);

    List<EventParticipantDAO> findByEventIdAndRsvpStatus(Long eventId, String rsvpStatus);
}
