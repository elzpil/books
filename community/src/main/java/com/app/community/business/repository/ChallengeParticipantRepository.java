package com.app.community.business.repository;

import com.app.community.business.repository.model.ChallengeParticipantDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeParticipantRepository extends JpaRepository<ChallengeParticipantDAO, Long> {
    List<ChallengeParticipantDAO> findByChallengeId(Long challengeId);
    List<ChallengeParticipantDAO> findByUserIdAndChallengeId(Long userId, Long challengeId);
}
