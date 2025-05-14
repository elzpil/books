package com.app.community.business.service;

import com.app.community.dto.ChallengeUpdateDTO;
import com.app.community.model.Challenge;

import java.util.List;
import java.util.Optional;

public interface ChallengeService {
    Challenge createChallenge(Challenge challenge, String token);
    List<Challenge> getAllChallenges();
    Optional<Challenge> getChallengeById(Long challengeId);
    Challenge updateChallenge(Long challengeId, ChallengeUpdateDTO challengeUpdateDTO, String token);
    void deleteChallenge(Long challengeId, String token);
    List<Challenge> searchChallenges(String name, String description);
    List<Challenge> getChallengesSortedByPopularity();
}
