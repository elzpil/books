package com.app.community.business.service;

import com.app.community.model.ChallengeParticipant;

import java.util.List;

public interface ChallengeParticipantService {
    ChallengeParticipant joinChallenge(Long challengeId, Long userId);
    List<ChallengeParticipant> getParticipants(Long challengeId);
    ChallengeParticipant updateProgress(Long challengeId, Long userId, int progress);
    void leaveChallenge(Long challengeId, Long userId);
}
