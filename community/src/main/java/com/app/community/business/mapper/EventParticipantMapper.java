package com.app.community.business.mapper;

import com.app.community.business.repository.model.EventParticipantDAO;
import com.app.community.model.EventParticipant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventParticipantMapper {

    EventParticipant eventParticipantDAOToEventParticipant(EventParticipantDAO eventParticipantDAO);

    EventParticipantDAO eventParticipantToEventParticipantDAO(EventParticipant eventParticipant);
}
