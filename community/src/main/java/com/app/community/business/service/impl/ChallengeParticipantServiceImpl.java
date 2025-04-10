package com.app.community.business.service.impl;

import com.app.community.auth.util.JwtTokenUtil;
import com.app.community.business.mapper.ChallengeMapper;
import com.app.community.business.mapper.ChallengeParticipantMapper;
import com.app.community.business.repository.ChallengeParticipantRepository;
import com.app.community.business.repository.ChallengeRepository;
import com.app.community.business.repository.model.ChallengeDAO;
import com.app.community.business.repository.model.ChallengeParticipantDAO;
import com.app.community.business.service.ChallengeParticipantService;
import com.app.community.dto.ChallengeParticipantUpdateDTO;
import com.app.community.exception.UnauthorizedException;
import com.app.community.model.Challenge;
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
    private final ChallengeRepository challengeRepository;

    private final ChallengeMapper challengeMapper;
    private final JwtTokenUtil jwtTokenUtil;

    public ChallengeParticipantServiceImpl(ChallengeParticipantRepository participantRepository,
                                           ChallengeParticipantMapper participantMapper,
                                           ChallengeRepository challengeRepository,
                                           ChallengeMapper challengeMapper,
                                           JwtTokenUtil jwtTokenUtil) {
        this.participantRepository = participantRepository;
        this.participantMapper = participantMapper;
        this.challengeRepository = challengeRepository;
        this.challengeMapper = challengeMapper;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public ChallengeParticipant joinChallenge(Long challengeId, String token) {
        log.info("Checking if challenge exists");
        Long userId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));

        List<ChallengeParticipantDAO> existingParticipants = participantRepository.findByUserIdAndChallengeId(userId, challengeId);
        if (!existingParticipants.isEmpty()) {
            throw new IllegalStateException("User is already a participant in this challenge");
        }

        log.info("Joining a challenge, user and challenge ids : {}, {}", userId, challengeId );
        ChallengeParticipantDAO participantDAO = new ChallengeParticipantDAO();
        participantDAO.setUserId(userId);
        participantDAO.setChallengeId(challengeId);
        log.info("saving to db, user and challenge ids : {}, {}", participantDAO.getUserId(), participantDAO.getChallengeId() );
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
    public ChallengeParticipant updateProgress(Long challengeId, ChallengeParticipantUpdateDTO challengeParticipantUpdateDTO,
                                               String token) {
        log.info("Updating progress");
        Long userId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));
        log.info("user from token " + userId);
        log.info("challenge id " + challengeId);
        List<ChallengeParticipantDAO> participants = participantRepository.findByUserIdAndChallengeId(userId, challengeId);
        if (participants.isEmpty()) {
            log.info("User is not a participant of this challenge");
            throw new IllegalStateException("User is not a participant of this challenge");
        }
        if (!isAuthorized(token, participants.get(0).getUserId())) {
            log.warn("Unauthorized attempt to update review ID {} by user ID {}", challengeId, userId);
            throw new UnauthorizedException("You are not authorized to update this review");
        }
        Integer progress = challengeParticipantUpdateDTO.getProgress();
        ChallengeParticipantDAO participantDAO = participants.get(0);
        participantDAO.setProgress(progress);
        participantDAO.setCompleted(progress == 100);
        return participantMapper.challengeParticipantDAOToChallengeParticipant(participantRepository.save(participantDAO));
    }

    @Override
    public void leaveChallenge(Long challengeId, String token) {
        Long userId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));
        List<ChallengeParticipantDAO> participants = participantRepository.findByUserIdAndChallengeId(userId, challengeId);
        if (!isAuthorized(token, participants.get(0).getUserId())) {
            log.warn("Unauthorized attempt to update review ID {} by user ID {}", challengeId, userId);
            throw new UnauthorizedException("You are not authorized to update this review");
        }

        if (participants.isEmpty()) {
            throw new IllegalStateException("User is not a participant of this challenge");
        }

        log.info("Leaving a challenge");
        participantRepository.delete(participants.get(0));
    }

    private boolean isAuthorized(String token, Long userId) {
        String cleanToken = token.replace("Bearer ", "");
        Long tokenUserId = jwtTokenUtil.extractUserId(cleanToken);
        String role = jwtTokenUtil.extractRole(cleanToken);
        return tokenUserId.equals(userId) || "ADMIN".equals(role);
    }

    @Override
    public List<Challenge> getUserChallenges(String token) {
        Long userId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));
        List<ChallengeParticipantDAO> participants = participantRepository.findByUserId(userId);
        List<Long> challengeIds = participants.stream()
                .map(ChallengeParticipantDAO::getChallengeId)
                .toList();

        List<ChallengeDAO> challenges = challengeRepository.findAllById(challengeIds);
        return challenges.stream()
                .map(challengeMapper::challengeDAOToChallenge)
                .toList();
    }

    @Override
    public ChallengeParticipant getParticipationDetails(Long challengeId, String token) {
        log.info("Getting participation details for challengeId: {}", challengeId);
        Long userId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));

        return participantRepository.findByChallengeIdAndUserId(challengeId, userId)
                .map(participantMapper::challengeParticipantDAOToChallengeParticipant)
                .orElse(null);
    }
}
