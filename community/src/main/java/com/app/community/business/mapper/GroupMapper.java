package com.app.community.business.mapper;

import com.app.community.business.repository.model.GroupDAO;
import com.app.community.model.Group;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    Group groupDAOToGroup(GroupDAO groupDAO);
    GroupDAO groupToGroupDAO (Group group);
}
