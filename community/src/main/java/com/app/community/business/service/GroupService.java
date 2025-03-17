package com.app.community.business.service;

import com.app.community.model.Group;

import java.util.List;
import java.util.Optional;

public interface GroupService {

    Group createGroup(Group group);

    List<Group> getAllGroups();

    Optional<Group> getGroupById(Long groupId);

    Group updateGroup(Long groupId, Group updatedGroup);

    void deleteGroup(Long groupId);
}
