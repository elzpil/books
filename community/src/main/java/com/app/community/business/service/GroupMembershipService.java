package com.app.community.business.service;

import com.app.community.model.GroupMembership;

import java.util.List;

public interface GroupMembershipService {
    GroupMembership joinGroup(GroupMembership groupMembership);
    List<GroupMembership> getGroupMembers(Long groupId);
    void leaveGroup(Long groupId, Long userId);

}
