package com.app.community.business.service.impl;

import com.app.community.business.mapper.ChallengeMapper;
import com.app.community.business.repository.ChallengeRepository;
import com.app.community.business.repository.model.ChallengeDAO;
import com.app.community.business.service.ChallengeService;
import com.app.community.model.Challenge;
import lombok.extern.slf4j.Slf4j;
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

    public ChallengeServiceImpl(ChallengeRepository challengeRepository,
                                ChallengeMapper challengeMapper,
                                UserServiceClient userServiceClient) {
        this.challengeRepository = challengeRepository;
        this.challengeMapper = challengeMapper;
        this.userServiceClient = userServiceClient;
    }


    @Override
    public Challenge createChallenge(Challenge challenge) {

        if (!userServiceClient.doesUserExist(challenge.getCreatorId())) {
            throw new IllegalArgumentException("User with ID " + challenge.getCreatorId() + " does not exist");
        }

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
    public Challenge updateChallenge(Long challengeId, Challenge challenge) {
        Optional<ChallengeDAO> existingChallengeOpt = challengeRepository.findById(challengeId);
        if (existingChallengeOpt.isPresent()) {
            log.info("Updating challenge ID {}", challengeId);
            ChallengeDAO existingChallenge = existingChallengeOpt.get();
            existingChallenge.setName(challenge.getName());
            existingChallenge.setDescription(challenge.getDescription());
            existingChallenge.setCreatorId(challenge.getCreatorId());
            existingChallenge.setStartDate(challenge.getStartDate());
            existingChallenge.setEndDate(challenge.getEndDate());
            return challengeMapper.challengeDAOToChallenge(challengeRepository.save(existingChallenge));
        }
        return null;
    }

    @Override
    public void deleteChallenge(Long challengeId) {
        log.info("Deleting challenge ID {}", challengeId);
        challengeRepository.deleteById(challengeId);
    }
}
