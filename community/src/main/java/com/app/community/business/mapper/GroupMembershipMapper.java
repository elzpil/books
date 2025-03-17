package com.app.community.business.mapper;

import com.app.community.business.repository.model.GroupMembershipDAO;
import com.app.community.model.GroupMembership;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GroupMembershipMapper {

    GroupMembership groupMembershipDAOToGroupMembership(GroupMembershipDAO groupMembershipDAO);

    GroupMembershipDAO groupMembershipToGroupMembershipDAO(GroupMembership groupMembership);
}
