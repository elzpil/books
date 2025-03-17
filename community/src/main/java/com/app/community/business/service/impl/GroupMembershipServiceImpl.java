package com.app.community.business.service.impl;

import com.app.community.business.mapper.GroupMembershipMapper;
import com.app.community.business.repository.GroupMembershipRepository;
import com.app.community.business.repository.model.GroupMembershipDAO;
import com.app.community.business.service.GroupMembershipService;
import com.app.community.model.GroupMembership;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class GroupMembershipServiceImpl implements GroupMembershipService {

    private final GroupMembershipRepository groupMembershipRepository;
    private final GroupMembershipMapper groupMembershipMapper;
    private final UserServiceClient userServiceClient;

    public GroupMembershipServiceImpl(GroupMembershipRepository groupMembershipRepository,
                                      GroupMembershipMapper groupMembershipMapper, UserServiceClient userServiceClient) {
        this.groupMembershipRepository = groupMembershipRepository;
        this.groupMembershipMapper = groupMembershipMapper;
        this.userServiceClient = userServiceClient;
    }

    @Override
    public GroupMembership joinGroup(GroupMembership groupMembership) {
        Long userId = groupMembership.getUserId();
        log.info("Validating user with ID: {}", userId);
        if (!userServiceClient.doesUserExist(userId)) {
            throw new IllegalArgumentException("User with ID " + userId + " does not exist.");
        }
        GroupMembershipDAO membershipDAO = new GroupMembershipDAO();
        membershipDAO.setGroupId(groupMembership.getGroupId());
        membershipDAO.setUserId(groupMembership.getUserId());
        membershipDAO.setRole(groupMembership.getRole());
        GroupMembershipDAO savedMembership = groupMembershipRepository.save(membershipDAO);
        log.info("Saving membership: {}", membershipDAO);
        return groupMembershipMapper.groupMembershipDAOToGroupMembership(savedMembership);
    }

    @Override
    @Transactional
    public void leaveGroup(Long groupId, Long userId) {
        log.info("Deleting membership : {}");
        groupMembershipRepository.deleteByGroupIdAndUserId(groupId, userId);
    }

    @Override
    public List<GroupMembership> getGroupMembers(Long groupId) {
        List<GroupMembershipDAO> memberships = groupMembershipRepository.findByGroupId(groupId);
        log.info("Getting memberships: {}");
        return memberships.stream()
                .map(groupMembershipMapper::groupMembershipDAOToGroupMembership)
                .toList();
    }
}
