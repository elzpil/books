package com.app.community.business.service.impl;

import com.app.community.auth.util.JwtTokenUtil;
import com.app.community.business.mapper.ChallengeMapper;
import com.app.community.business.repository.ChallengeRepository;
import com.app.community.business.repository.model.ChallengeDAO;
import com.app.community.business.service.ChallengeService;
import com.app.community.dto.ChallengeUpdateDTO;
import com.app.community.exception.ResourceNotFoundException;
import com.app.community.exception.UnauthorizedException;
import com.app.community.model.Challenge;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.preauth.j2ee.J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeMapper challengeMapper;

    private final UserServiceClient userServiceClient;
    private final JwtTokenUtil jwtTokenUtil;

    public ChallengeServiceImpl(ChallengeRepository challengeRepository,
                                ChallengeMapper challengeMapper,
                                UserServiceClient userServiceClient,
                                JwtTokenUtil jwtTokenUtil) {
        this.challengeRepository = challengeRepository;
        this.challengeMapper = challengeMapper;
        this.userServiceClient = userServiceClient;
        this.jwtTokenUtil = jwtTokenUtil;
    }


    @Override
    public Challenge createChallenge(Challenge challenge, String token) {

        Long creatorId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));
        challenge.setCreatorId(creatorId);
        ChallengeDAO challengeDAO = challengeRepository.save(challengeMapper.challengeToChallengeDAO(challenge));
        log.info("Saving new challenge: {}", challengeDAO);
        return challengeMapper.challengeDAOToChallenge(challengeDAO);
    }

    @Override
    public List<Challenge> getAllChallenges() {
        log.info("Retrieving challenges");
        return challengeRepository.findAll().stream()
                .map(challengeMapper::challengeDAOToChallenge)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Challenge> getChallengeById(Long challengeId) {
        return challengeRepository.findById(challengeId).map(challengeMapper::challengeDAOToChallenge);
    }

    @Override
    @Transactional
    public Challenge updateChallenge(Long challengeId, ChallengeUpdateDTO challengeUpdateDTO, String token) {
        log.info("Updating challenge ID {}: {}", challengeId, challengeUpdateDTO);

        ChallengeDAO existingChallenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge", challengeId));

        // Authorization check
        if (!isAuthorized(token, existingChallenge.getCreatorId())) {
            log.warn("Unauthorized attempt to update challenge ID {} by user ID {}", challengeId, jwtTokenUtil.extractUserId(token.replace("Bearer ", "")));
            throw new UnauthorizedException("You are not authorized to update this challenge");
        }

        if (challengeUpdateDTO.getName() != null) {
            existingChallenge.setName(challengeUpdateDTO.getName());
        }

        if (challengeUpdateDTO.getDescription() != null) {
            existingChallenge.setDescription(challengeUpdateDTO.getDescription());
        }

        if (challengeUpdateDTO.getStartDate() != null) {
            existingChallenge.setStartDate(challengeUpdateDTO.getStartDate());
        }

        if (challengeUpdateDTO.getEndDate() != null) {
            existingChallenge.setEndDate(challengeUpdateDTO.getEndDate());
        }

        ChallengeDAO updatedChallenge = challengeRepository.save(existingChallenge);
        log.info("Successfully updated challenge ID {}", challengeId);

        return challengeMapper.challengeDAOToChallenge(updatedChallenge);
    }


    @Override
    public void deleteChallenge(Long challengeId, String token) {

        ChallengeDAO existingChallenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge", challengeId));

        // Authorization check
        if (!isAuthorized(token, existingChallenge.getCreatorId())) {
            log.warn("Unauthorized attempt to update challenge ID {} by user ID {}", challengeId, jwtTokenUtil.extractUserId(token.replace("Bearer ", "")));
            throw new UnauthorizedException("You are not authorized to update this challenge");
        }

        log.info("Deleting challenge ID {}", challengeId);
        challengeRepository.deleteById(challengeId);
    }



    private boolean isAuthorized(String token, Long userId) {
        String cleanToken = token.replace("Bearer ", "");
        Long tokenUserId = jwtTokenUtil.extractUserId(cleanToken);
        String role = jwtTokenUtil.extractRole(cleanToken);
        return tokenUserId.equals(userId) || "ADMIN".equals(role);
    }
}
