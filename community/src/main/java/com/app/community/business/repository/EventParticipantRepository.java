package com.app.community.business.repository;

import com.app.community.business.repository.model.EventParticipantDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipantDAO, Long> {

    List<EventParticipantDAO> findByEventId(Long eventId);

    List<EventParticipantDAO> findByEventIdAndRsvpStatus(Long eventId, String rsvpStatus);
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
    Optional<EventParticipantDAO> findByUserIdAndEventId(Long userId, Long eventId);

}
