package com.app.community.business.mapper;

import com.app.community.business.repository.model.ChallengeDAO;
import com.app.community.model.Challenge;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChallengeMapper {
    Challenge challengeDAOToChallenge(ChallengeDAO challengeDAO);
    ChallengeDAO challengeToChallengeDAO(Challenge challenge);
}
