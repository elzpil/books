package com.app.community.business.service.impl;

import com.app.community.business.mapper.ChallengeParticipantMapper;
import com.app.community.business.repository.ChallengeParticipantRepository;
import com.app.community.business.repository.model.ChallengeParticipantDAO;
import com.app.community.business.service.ChallengeParticipantService;
import com.app.community.model.ChallengeParticipant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ChallengeParticipantServiceImpl implements ChallengeParticipantService {

    private final ChallengeParticipantRepository participantRepository;
    private final ChallengeParticipantMapper participantMapper;
    private final UserServiceClient userServiceClient;

    public ChallengeParticipantServiceImpl(ChallengeParticipantRepository participantRepository,
                                           ChallengeParticipantMapper participantMapper,
                                           UserServiceClient userServiceClient) {
        this.participantRepository = participantRepository;
        this.participantMapper = participantMapper;
        this.userServiceClient = userServiceClient;
    }

    @Override
    public ChallengeParticipant joinChallenge(Long challengeId, Long userId) {
        log.info("Checking if challenge exists");

        if (!userServiceClient.doesUserExist(userId)) {
            throw new IllegalArgumentException("User with ID " + userId + " does not exist");
        }

        List<ChallengeParticipantDAO> existingParticipants = participantRepository.findByUserIdAndChallengeId(userId, challengeId);
        if (!existingParticipants.isEmpty()) {
            throw new IllegalStateException("User is already a participant in this challenge");
        }

        log.info("Joining a challenge, user and challenge ids : {}, {}", userId, challengeId );
        ChallengeParticipantDAO participantDAO = new ChallengeParticipantDAO();
        participantDAO.setUser(userId);
        participantDAO.setChallenge(challengeId);
        log.info("saving to db, user and challenge ids : {}, {}", participantDAO.getUser(), participantDAO.getChallenge() );
        participantDAO.setProgress(0);
        participantDAO.setCompleted(false);
        participantDAO.setJoinedAt(java.time.LocalDateTime.now());

        return participantMapper.challengeParticipantDAOToChallengeParticipant(participantRepository.save(participantDAO));
    }

    @Override
    public List<ChallengeParticipant> getParticipants(Long challengeId) {
        log.info("Getting participants");
        List<ChallengeParticipantDAO> participants = participantRepository.findByChallengeId(challengeId);
        return participants.stream()
                .map(participantMapper::challengeParticipantDAOToChallengeParticipant)
                .toList();
    }

    @Transactional
    @Override
    public ChallengeParticipant updateProgress(Long challengeId, Long userId, int progress) {
        log.info("Updating progress");
        List<ChallengeParticipantDAO> participants = participantRepository.findByUserIdAndChallengeId(userId, challengeId);
        if (participants.isEmpty()) {
            throw new IllegalStateException("User is not a participant of this challenge");
        }

        ChallengeParticipantDAO participantDAO = participants.get(0);
        participantDAO.setProgress(progress);
        participantDAO.setCompleted(progress == 100);
        return participantMapper.challengeParticipantDAOToChallengeParticipant(participantRepository.save(participantDAO));
    }

    @Override
    public void leaveChallenge(Long challengeId, Long userId) {
        List<ChallengeParticipantDAO> participants = participantRepository.findByUserIdAndChallengeId(userId, challengeId);
        if (participants.isEmpty()) {
            throw new IllegalStateException("User is not a participant of this challenge");
        }
        log.info("Leaving a challenge");
        participantRepository.delete(participants.get(0));
    }
}
