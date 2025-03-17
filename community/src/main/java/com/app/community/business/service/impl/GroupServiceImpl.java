package com.app.community.business.service.impl;

import com.app.community.business.mapper.GroupMapper;
import com.app.community.business.repository.GroupRepository;
import com.app.community.business.repository.model.GroupDAO;
import com.app.community.business.service.GroupService;
import com.app.community.model.Group;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;
    private final UserServiceClient userServiceClient;

    public GroupServiceImpl(GroupRepository groupRepository,
                            GroupMapper groupMapper,
                            UserServiceClient userServiceClient) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
        this.userServiceClient = userServiceClient;
    }

    @Override
    public Group createGroup(Group group) {

        if (!userServiceClient.doesUserExist(group.getCreatorId())) {
            throw new IllegalArgumentException("User with ID " + group.getCreatorId() + " does not exist.");
        }

        GroupDAO groupDAO = groupMapper.groupToGroupDAO(group);
        groupDAO.setCreatedAt(java.time.LocalDateTime.now());
        GroupDAO savedGroup = groupRepository.save(groupDAO);
        log.info("Saving group: {}", groupDAO);
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
    public Group updateGroup(Long groupId, Group updatedGroup) {
        Optional<GroupDAO> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isPresent()) {
            GroupDAO existingGroup = groupOpt.get();
            existingGroup.setName(updatedGroup.getName());
            existingGroup.setDescription(updatedGroup.getDescription());
            existingGroup.setPrivacySetting(updatedGroup.getPrivacySetting());  // Store enum as string
            GroupDAO updatedGroupDAO = groupRepository.save(existingGroup);
            log.info("Updating group: {}", updatedGroupDAO);
            return groupMapper.groupDAOToGroup(updatedGroupDAO);
        }
        return null;
    }

    @Override
    public void deleteGroup(Long groupId) {
        log.info("Deleting group with id: {}", groupId);
        groupRepository.deleteById(groupId);
    }
}
