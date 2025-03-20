package com.app.community.business.service;

import com.app.community.dto.ChallengeParticipantUpdateDTO;
import com.app.community.model.ChallengeParticipant;

import java.util.List;

public interface ChallengeParticipantService {
    ChallengeParticipant joinChallenge(Long challengeId, String token);
    List<ChallengeParticipant> getParticipants(Long challengeId);
    ChallengeParticipant updateProgress(Long challengeId, ChallengeParticipantUpdateDTO challengeParticipantUpdateDTO,
                                        String token);
    void leaveChallenge(Long challengeId, String token);
}
