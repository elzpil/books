package com.app.community.controller;

import com.app.community.business.service.GroupMembershipService;
import com.app.community.dto.EventUpdateDTO;
import com.app.community.dto.GroupMembershipAddDTO;
import com.app.community.dto.GroupMembershipUpdateDTO;
import com.app.community.model.Event;
import com.app.community.model.Group;
import com.app.community.model.GroupMembership;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

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
    @GetMapping("/my")
    public ResponseEntity<List<Group>> getUserGroups(@RequestHeader("Authorization") String token) {
        List<Group> userGroups = groupMembershipService.getGroupsForUser(token);
        return ResponseEntity.ok(userGroups);
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkIfUserIsMember(@PathVariable Long groupId, @RequestParam Long userId,  @RequestHeader("Authorization") String token) {
        Optional<GroupMembership> membership = groupMembershipService.findByGroupIdAndUserId(groupId, userId);
        return ResponseEntity.ok(membership.isPresent());
    }

    @DeleteMapping
    public ResponseEntity<Void> removeMember(@PathVariable Long groupId,
                                             @RequestParam Long userId,
                                             @RequestHeader("Authorization") String token) {
        groupMembershipService.removeMember(groupId, userId, token);
        log.info("Member with ID {} removed from group ID {}", userId, groupId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/add")
    public ResponseEntity<GroupMembership> addMemberToGroup(@PathVariable Long groupId,
                                                            @Valid @RequestBody GroupMembershipAddDTO dto,
                                                            @RequestHeader("Authorization") String token) {
        GroupMembership addedMembership = groupMembershipService.addMemberToGroup(groupId, dto, token);
        log.info("Admin added user ID {} to group ID {} as {}", dto.getUserId(), groupId, dto.getRole());
        return ResponseEntity.ok(addedMembership);
    }


}
