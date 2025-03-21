package com.app.community.business.service;

import com.app.community.dto.GroupMembershipUpdateDTO;
import com.app.community.model.GroupMembership;

import java.util.List;

public interface GroupMembershipService {
    GroupMembership joinGroup(Long groupId, GroupMembership groupMembership, String token);
    List<GroupMembership> getGroupMembers(Long groupId);
    void leaveGroup(Long groupId, String token);
    GroupMembership updateGroupMembership(Long groupId, GroupMembershipUpdateDTO groupMembershipUpdateDTO,
                                          String token);

}
