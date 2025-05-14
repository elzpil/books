package com.app.community.business.service;

import com.app.community.dto.GroupUpdateDTO;
import com.app.community.model.Group;

import java.util.List;
import java.util.Optional;

public interface GroupService {

    Group createGroup(Group group, String token);
    List<Group> getAllGroups();
    Optional<Group> getGroupById(Long groupId);
    Group updateGroup(Long groupId, GroupUpdateDTO groupUpdateDTO, String token);
    void deleteGroup(Long groupId, String token);
    List<Group> searchGroupsByName(String name);
    List<Group> searchPublicGroups(String query);
}
