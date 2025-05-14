package com.app.community.business.mapper;

import com.app.community.business.repository.model.EventDAO;
import com.app.community.model.Event;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {

    Event eventDAOToEvent(EventDAO eventDAO);
    EventDAO eventToEventDAO(Event event);
}
