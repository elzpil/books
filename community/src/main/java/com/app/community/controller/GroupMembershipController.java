package com.app.community.controller;

import com.app.community.business.service.GroupMembershipService;
import com.app.community.dto.EventUpdateDTO;
import com.app.community.dto.GroupMembershipUpdateDTO;
import com.app.community.model.Event;
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
    public ResponseEntity<GroupMembership> joinGroup( @PathVariable Long groupId,
                                                      @Valid @RequestBody GroupMembership membership,
                                                      @RequestHeader("Authorization") String token) {

        GroupMembership savedMembership = groupMembershipService.joinGroup(groupId, membership, token);
        log.info("User joining group with id: {}", membership.getGroupId());

        return ResponseEntity.ok(savedMembership);
    }


    @DeleteMapping("/leave")
    public ResponseEntity<Void> leaveGroup(@PathVariable Long groupId,
                                           @RequestHeader("Authorization") String token) {
        groupMembershipService.leaveGroup(groupId, token);
        log.info("Leaving a group with id: {}", groupId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<GroupMembership>> getGroupMembers(@PathVariable Long groupId) {
        List<GroupMembership> members = groupMembershipService.getGroupMembers(groupId);
        log.info("Getting group with id: {} members", groupId);
        return ResponseEntity.ok(members);
    }

    @PutMapping
    public ResponseEntity<GroupMembership> updateGroupMembership(@PathVariable Long groupId,
                                                                 @Valid @RequestBody GroupMembershipUpdateDTO groupMembershipUpdateDTO,
                                             @RequestHeader("Authorization") String token) {

        log.info("Updating group membership");
        GroupMembership updatedGroupMembership = groupMembershipService.updateGroupMembership(groupId, groupMembershipUpdateDTO, token);
        return updatedGroupMembership != null ? ResponseEntity.ok(updatedGroupMembership) : ResponseEntity.notFound().build();
    }
}
