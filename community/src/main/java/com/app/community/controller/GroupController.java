package com.app.community.controller;

import com.app.community.business.service.GroupService;
import com.app.community.dto.GroupUpdateDTO;
import com.app.community.model.Group;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;


    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody Group group,
                                             @RequestHeader("Authorization") String token) {
        Group createdGroup = groupService.createGroup(group, token);
        log.info("Creating group");
        return ResponseEntity.ok(createdGroup);
    }

    @GetMapping
    public ResponseEntity<List<Group>> getAllGroups() {
        List<Group> groups = groupService.getAllGroups();
        log.info("Getting all groups");
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<Group> getGroupById(@PathVariable Long groupId) {
        Optional<Group> group = groupService.getGroupById(groupId);
        log.info("Getting a group with id: {}", groupId);
        return group.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<Group> updateGroup(@PathVariable Long groupId, @RequestBody GroupUpdateDTO groupUpdateDTO,
                                             @RequestHeader("Authorization") String token) {
        Group updated = groupService.updateGroup(groupId, groupUpdateDTO, token);
        log.info("Updating a group with id: {}", groupId);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long groupId,
                                            @RequestHeader("Authorization") String token) {
        groupService.deleteGroup(groupId, token);
        log.info("Deleting a group with id: {}", groupId);
        return ResponseEntity.noContent().build();
    }
}
