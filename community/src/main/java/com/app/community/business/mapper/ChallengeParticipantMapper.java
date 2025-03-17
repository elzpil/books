package com.app.community.business.mapper;

import com.app.community.business.repository.model.ChallengeParticipantDAO;
import com.app.community.model.ChallengeParticipant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChallengeParticipantMapper {

    @Mapping(source = "user", target = "userId") // Maps 'user' field to 'userId'
    @Mapping(source = "challenge", target = "challengeId") // Maps 'challenge' field to 'challengeId'
    ChallengeParticipant challengeParticipantDAOToChallengeParticipant(ChallengeParticipantDAO challengeParticipantDAO);

    @Mapping(source = "userId", target = "user") // Maps 'userId' to 'user'
    @Mapping(source = "challengeId", target = "challenge") // Maps 'challengeId' to 'challenge'
    ChallengeParticipantDAO challengeParticipantToChallengeParticipantDAO(ChallengeParticipant challengeParticipant);
}

