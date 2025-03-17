package com.app.community.business.service;

import com.app.community.model.Challenge;

import java.util.List;
import java.util.Optional;

public interface ChallengeService {
    Challenge createChallenge(Challenge challenge);
    List<Challenge> getAllChallenges();
    Optional<Challenge> getChallengeById(Long challengeId);
    Challenge updateChallenge(Long challengeId, Challenge challenge);
    void deleteChallenge(Long challengeId);
}
