package com.app.community.business.service.impl;

import com.app.community.auth.util.JwtTokenUtil;
import com.app.community.business.mapper.GroupMembershipMapper;
import com.app.community.business.repository.GroupMembershipRepository;
import com.app.community.business.repository.model.EventDAO;
import com.app.community.business.repository.model.GroupDAO;
import com.app.community.business.repository.model.GroupMembershipDAO;
import com.app.community.business.service.GroupMembershipService;
import com.app.community.dto.EventUpdateDTO;
import com.app.community.dto.GroupMembershipUpdateDTO;
import com.app.community.exception.ResourceNotFoundException;
import com.app.community.exception.UnauthorizedException;
import com.app.community.model.Event;
import com.app.community.model.GroupMembership;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
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
    public GroupMembership updateGroupMembership(Long groupId, GroupMembershipUpdateDTO groupMembershipUpdateDTO,
                                       String token) {

        Long userId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));

        GroupMembershipDAO existingMembership = groupMembershipRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership with group " , groupId));

        if (!isAuthorized(token, existingMembership.getUserId())) {
            log.warn("Unauthorized attempt to update mmebership in group ID {} by user ID {}", groupId, userId);
            throw new UnauthorizedException("You are not authorized to update this group membership");
        }

        if (groupMembershipUpdateDTO.getRole() != null) {
            existingMembership.setRole(groupMembershipUpdateDTO.getRole());
        }

        GroupMembershipDAO updatedGroupMembership = groupMembershipRepository.save(existingMembership );
        log.info("Updating group membership: {}", updatedGroupMembership);
        return groupMembershipMapper.groupMembershipDAOToGroupMembership(updatedGroupMembership);
    }

    private boolean isAuthorized(String token, Long userId) {
        Long tokenUserId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));
        String role = jwtTokenUtil.extractRole(token.replace("Bearer ", ""));
        return tokenUserId.equals(userId) || "ADMIN".equals(role);
    }
}
