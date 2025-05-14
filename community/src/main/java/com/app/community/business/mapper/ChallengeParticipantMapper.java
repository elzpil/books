package com.app.community.business.mapper;

import com.app.community.business.repository.model.ChallengeParticipantDAO;
import com.app.community.model.ChallengeParticipant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChallengeParticipantMapper {

    ChallengeParticipant challengeParticipantDAOToChallengeParticipant(ChallengeParticipantDAO challengeParticipantDAO);
    ChallengeParticipantDAO challengeParticipantToChallengeParticipantDAO(ChallengeParticipant challengeParticipant);
}

