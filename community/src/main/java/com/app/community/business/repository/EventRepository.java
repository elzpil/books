package com.app.community.business.repository;

import com.app.community.business.repository.model.EventDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventDAO, Long> {
    // Find events by groupId
    List<EventDAO> findByGroupId(Long groupId);

    // Find events by groupId and eventDate between a range
    List<EventDAO> findByGroupIdAndEventDateBetween(Long groupId, LocalDateTime startDate, LocalDateTime endDate);
}
