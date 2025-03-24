package com.app.community.business.service.impl;

import com.app.community.auth.util.JwtTokenUtil;
import com.app.community.business.mapper.GroupMapper;
import com.app.community.business.repository.GroupRepository;
import com.app.community.business.repository.model.GroupDAO;
import com.app.community.business.service.GroupService;
import com.app.community.dto.GroupUpdateDTO;
import com.app.community.exception.ResourceNotFoundException;
import com.app.community.exception.UnauthorizedException;
import com.app.community.model.Group;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;
    private final JwtTokenUtil jwtTokenUtil;

    public GroupServiceImpl(GroupRepository groupRepository,
                            GroupMapper groupMapper,
                            JwtTokenUtil jwtTokenUtil) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public Group createGroup(Group group, String token) {

        Long userId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));
        group.setCreatorId(userId);
        group.setCreatedAt(LocalDateTime.now());
        GroupDAO savedGroup = groupRepository.save(groupMapper.groupToGroupDAO(group));
        log.info("Saving group: {}", savedGroup);
        return groupMapper.groupDAOToGroup(savedGroup);
    }

    @Override
    public List<Group> getAllGroups() {
        List<GroupDAO> groups = groupRepository.findAll();
        return groups.stream()
                .map(groupMapper::groupDAOToGroup)
                .toList();
    }

    @Override
    public Optional<Group> getGroupById(Long groupId) {
        Optional<GroupDAO> group = groupRepository.findById(groupId);
        log.info("Getting group with id: {}", groupId);
        return group.map(groupMapper::groupDAOToGroup);
    }

    @Transactional
    @Override
    public Group updateGroup(Long groupId, GroupUpdateDTO groupUpdateDTO, String token) {

        GroupDAO existingGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", groupId));

        // Check if the user is authorized
        if (!isAuthorized(token, existingGroup.getCreatorId())) {
            log.warn("Unauthorized attempt to delete group ID {} by user ID {}", groupId, existingGroup.getCreatorId());
            throw new UnauthorizedException("You are not authorized to delete this group");
        }

        if (groupUpdateDTO.getName() != null) {
            existingGroup.setName(groupUpdateDTO.getName());
        }
        if (groupUpdateDTO.getDescription() != null) {
            existingGroup.setDescription(groupUpdateDTO.getDescription());
        }
        if (groupUpdateDTO.getPrivacySetting() != null) {
            existingGroup.setPrivacySetting(groupUpdateDTO.getPrivacySetting());
        }

        GroupDAO updatedGroupDAO = groupRepository.save(existingGroup);
        log.info("Updating group: {}", updatedGroupDAO);
        return groupMapper.groupDAOToGroup(updatedGroupDAO);
    }

    @Override
    public void deleteGroup(Long groupId, String token) {
        GroupDAO existingGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", groupId));

        // Check if the user is authorized
        if (!isAuthorized(token, existingGroup.getCreatorId())) {
            log.warn("Unauthorized attempt to delete group ID {} by user ID {}", groupId, existingGroup.getCreatorId());
            throw new UnauthorizedException("You are not authorized to delete this group");
        }
        log.info("Deleting group with id: {}", groupId);
        groupRepository.deleteById(groupId);
    }

    private boolean isAuthorized(String token, Long userId) {
        Long tokenUserId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));
        String role = jwtTokenUtil.extractRole(token.replace("Bearer ", ""));
        return tokenUserId.equals(userId) || "ADMIN".equals(role);
    }

    @Override
    public List<Group> searchGroupsByName(String name) {
        log.info("Searching for groups with name: {}", name);
        return groupRepository.searchGroupsByName(name).stream()
                .map(groupMapper::groupDAOToGroup)
                .collect(Collectors.toList());
    }

}
