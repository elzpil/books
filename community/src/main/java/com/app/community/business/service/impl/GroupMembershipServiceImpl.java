package com.app.community.business.service.impl;

import com.app.community.auth.util.JwtTokenUtil;
import com.app.community.business.mapper.GroupMembershipMapper;
import com.app.community.business.mapper.GroupMapper;
import com.app.community.business.repository.GroupMembershipRepository;
import com.app.community.business.repository.GroupRepository;
import com.app.community.business.repository.model.EventDAO;
import com.app.community.business.repository.model.GroupDAO;
import com.app.community.business.repository.model.GroupMembershipDAO;
import com.app.community.business.service.GroupMembershipService;
import com.app.community.dto.EventUpdateDTO;
import com.app.community.dto.GroupMembershipAddDTO;
import com.app.community.dto.GroupMembershipUpdateDTO;
import com.app.community.exception.ResourceNotFoundException;
import com.app.community.exception.UnauthorizedException;
import com.app.community.model.Event;
import com.app.community.model.Group;
import com.app.community.model.GroupMembership;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GroupMembershipServiceImpl implements GroupMembershipService {

    private final GroupMembershipRepository groupMembershipRepository;
    private final GroupMembershipMapper groupMembershipMapper;
    private final JwtTokenUtil jwtTokenUtil;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private GroupMapper groupMapper;

    public GroupMembershipServiceImpl(GroupMembershipRepository groupMembershipRepository,
                                      GroupMembershipMapper groupMembershipMapper, JwtTokenUtil jwtTokenUtil) {
        this.groupMembershipRepository = groupMembershipRepository;
        this.groupMembershipMapper = groupMembershipMapper;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public GroupMembership joinGroup(Long groupId, GroupMembership groupMembership, String token) {
        Long userId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));

        Optional<GroupMembershipDAO> existingMembership = groupMembershipRepository.findByGroupIdAndUserId(groupId, userId);
        if (existingMembership.isPresent()) {
            log.info("User {} is already a member of group {}", userId, groupId);
            return groupMembershipMapper.groupMembershipDAOToGroupMembership(existingMembership.get());
        }

        groupMembership.setUserId(userId);
        groupMembership.setGroupId(groupId);
        groupMembership.setJoinedAt(LocalDateTime.now());

        GroupMembershipDAO membershipDAO = groupMembershipMapper.groupMembershipToGroupMembershipDAO(groupMembership);

        GroupMembershipDAO savedMembership = groupMembershipRepository.save(membershipDAO);
        log.info("User {} joined group {} as {}", userId, groupId, membershipDAO.getRole());

        return groupMembershipMapper.groupMembershipDAOToGroupMembership(savedMembership);
    }


    @Override
    @Transactional
    public void leaveGroup(Long groupId, String token) {
        Long userId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));

        GroupMembershipDAO existingMembership = groupMembershipRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership with group " , groupId));

        if (!isAuthorized(token, existingMembership.getUserId())) {
            log.warn("Unauthorized attempt to leave group ID {} by user ID {}", groupId, userId);
            throw new UnauthorizedException("You are not authorized to leave this group");
        }

        // Delete membership
        log.info("Deleting membership for user ID {} in group ID {}", userId, groupId);
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

    @Transactional
    @Override
    public GroupMembership updateGroupMembership(Long groupId, GroupMembershipUpdateDTO dto, String token) {
        Long currentUserId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));

        GroupMembershipDAO requesterMembership = groupMembershipRepository
                .findByGroupIdAndUserId(groupId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Your membership with group ", groupId));

        if (!requesterMembership.getRole().equalsIgnoreCase("admin")) {
            log.warn("User {} attempted to update role in group {} without admin rights", currentUserId, groupId);
            throw new UnauthorizedException("Only group admins can update roles of other members.");
        }

        GroupMembershipDAO targetMembership = groupMembershipRepository
                .findByGroupIdAndUserId(groupId, dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Target membership in group ", groupId));

        targetMembership.setRole(dto.getRole());

        GroupMembershipDAO updated = groupMembershipRepository.save(targetMembership);
        log.info("Admin {} updated role of user {} to {} in group {}", currentUserId, dto.getUserId(), dto.getRole(), groupId);

        return groupMembershipMapper.groupMembershipDAOToGroupMembership(updated);
    }


    private boolean isAuthorized(String token, Long userId) {
        Long tokenUserId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));
        String role = jwtTokenUtil.extractRole(token.replace("Bearer ", ""));
        return tokenUserId.equals(userId) || "ADMIN".equals(role);
    }



    @Override
    public List<Group> getGroupsForUser(String token) {
        Long userId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));
        List<GroupMembershipDAO> memberships = groupMembershipRepository.findByUserId(userId);

        List<Long> groupIds = memberships.stream()
                .map(GroupMembershipDAO::getGroupId)
                .toList();

        List<GroupDAO> groups = groupRepository.findAllById(groupIds);

        return groups.stream()
                .map(groupMapper::groupDAOToGroup)
                .toList();
    }

    @Override
    public Optional<GroupMembership> findByGroupIdAndUserId(Long groupId, Long userId) {
        Optional<GroupMembershipDAO> membershipDAO = groupMembershipRepository.findByGroupIdAndUserId(groupId, userId);
        return membershipDAO.map(groupMembershipMapper::groupMembershipDAOToGroupMembership);
    }

    @Override
    @Transactional
    public void removeMember(Long groupId, Long targetUserId, String token) {
        Long currentUserId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));

        GroupMembershipDAO requesterMembership = groupMembershipRepository.findByGroupIdAndUserId(groupId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Your membership with group ", groupId));

        if (!requesterMembership.getRole().equalsIgnoreCase("admin")) {
            log.warn("Unauthorized attempt by user ID {} to remove user ID {} from group ID {}", currentUserId, targetUserId, groupId);
            throw new UnauthorizedException("Only admins can remove members from the group.");
        }

        GroupMembershipDAO targetMembership = groupMembershipRepository.findByGroupIdAndUserId(groupId, targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Target membership not found in group ", groupId));

        groupMembershipRepository.deleteByGroupIdAndUserId(groupId, targetUserId);
        log.info("Admin user ID {} removed user ID {} from group ID {}", currentUserId, targetUserId, groupId);
    }

    @Override
    @Transactional
    public GroupMembership addMemberToGroup(Long groupId, GroupMembershipAddDTO dto, String token) {
        Long currentUserId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));

        GroupMembershipDAO adminMembership = groupMembershipRepository.findByGroupIdAndUserId(groupId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Your membership with group ", groupId));

        if (!adminMembership.getRole().equalsIgnoreCase("admin")) {
            throw new UnauthorizedException("Only group admins can add members.");
        }

        Optional<GroupMembershipDAO> existing = groupMembershipRepository.findByGroupIdAndUserId(groupId, dto.getUserId());
        if (existing.isPresent()) {
            log.info("User {} already exists in group {}", dto.getUserId(), groupId);
            return groupMembershipMapper.groupMembershipDAOToGroupMembership(existing.get());
        }

        GroupMembership membership = new GroupMembership();
        membership.setGroupId(groupId);
        membership.setUserId(dto.getUserId());
        membership.setRole(dto.getRole());
        membership.setJoinedAt(LocalDateTime.now());

        GroupMembershipDAO saved = groupMembershipRepository.save(
                groupMembershipMapper.groupMembershipToGroupMembershipDAO(membership)
        );

        return groupMembershipMapper.groupMembershipDAOToGroupMembership(saved);
    }


}
