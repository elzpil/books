package com.app.community.controller;

import com.app.community.business.service.GroupMembershipService;
import com.app.community.model.GroupMembership;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/groups/{groupId}/members")
public class GroupMembershipController {

    private final GroupMembershipService groupMembershipService;

    public GroupMembershipController(GroupMembershipService groupMembershipService) {
        this.groupMembershipService = groupMembershipService;
    }

    @PostMapping("/join")
    public ResponseEntity<GroupMembership> joinGroup( @Valid @RequestBody GroupMembership membership) {

        GroupMembership savedMembership = groupMembershipService.joinGroup(membership);
        log.info("User {} joining group with id: {}", membership.getUserId(), membership.getGroupId());

        return ResponseEntity.ok(savedMembership);
    }


    @DeleteMapping("/leave")
    public ResponseEntity<Void> leaveGroup(@PathVariable Long groupId,
                                           @RequestParam Long userId) {
        groupMembershipService.leaveGroup(groupId, userId);
        log.info("Leaving a group with id: {}", groupId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<GroupMembership>> getGroupMembers(@PathVariable Long groupId) {
        List<GroupMembership> members = groupMembershipService.getGroupMembers(groupId);
        log.info("Getting group with id: {} members", groupId);
        return ResponseEntity.ok(members);
    }
}
