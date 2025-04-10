package com.app.community.business.service;

import com.app.community.dto.GroupMembershipAddDTO;
import com.app.community.dto.GroupMembershipUpdateDTO;
import com.app.community.model.Group;
import com.app.community.model.GroupMembership;

import java.util.List;
import java.util.Optional;

public interface GroupMembershipService {
    GroupMembership joinGroup(Long groupId, GroupMembership groupMembership, String token);
    List<GroupMembership> getGroupMembers(Long groupId);
    void leaveGroup(Long groupId, String token);
    GroupMembership updateGroupMembership(Long groupId, GroupMembershipUpdateDTO groupMembershipUpdateDTO,
                                          String token);
    List<Group> getGroupsForUser(String token);

    Optional<GroupMembership> findByGroupIdAndUserId(Long groupId, Long userId);
    void removeMember(Long groupId, Long targetUserId, String token);
    GroupMembership addMemberToGroup(Long groupId, GroupMembershipAddDTO dto, String token);


}
