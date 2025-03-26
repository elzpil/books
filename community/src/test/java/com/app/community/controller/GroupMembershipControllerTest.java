package com.app.community.controller;

import com.app.community.business.service.GroupMembershipService;
import com.app.community.dto.GroupMembershipUpdateDTO;
import com.app.community.model.GroupMembership;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class GroupMembershipControllerTest {

    @Mock
    private GroupMembershipService groupMembershipService;

    @InjectMocks
    private GroupMembershipController groupMembershipController;

    private GroupMembership groupMembership;
    private GroupMembershipUpdateDTO groupMembershipUpdateDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        groupMembership = new GroupMembership();
        groupMembership.setId(1L);
        groupMembership.setUserId(100L);
        groupMembership.setGroupId(1L);
        groupMembership.setRole("member");
        groupMembership.setJoinedAt(LocalDateTime.now());

        groupMembershipUpdateDTO = new GroupMembershipUpdateDTO();
        groupMembershipUpdateDTO.setRole("admin");
    }

    @Test
    void testJoinGroup() {
        String token = "Bearer token";
        when(groupMembershipService.joinGroup(eq(1L), any(GroupMembership.class), eq(token))).thenReturn(groupMembership);

        ResponseEntity<GroupMembership> response = groupMembershipController.joinGroup(1L, groupMembership, token);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(groupMembership, response.getBody());
        verify(groupMembershipService, times(1)).joinGroup(eq(1L), any(GroupMembership.class), eq(token));
    }

    @Test
    void testLeaveGroup() {
        String token = "Bearer token";
        doNothing().when(groupMembershipService).leaveGroup(eq(1L), eq(token));

        ResponseEntity<Void> response = groupMembershipController.leaveGroup(1L, token);

        assertEquals(204, response.getStatusCodeValue());
        verify(groupMembershipService, times(1)).leaveGroup(eq(1L), eq(token));
    }

    @Test
    void testGetGroupMembers() {
        List<GroupMembership> members = List.of(groupMembership);
        when(groupMembershipService.getGroupMembers(eq(1L))).thenReturn(members);

        ResponseEntity<List<GroupMembership>> response = groupMembershipController.getGroupMembers(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        assertEquals(groupMembership, response.getBody().get(0));
        verify(groupMembershipService, times(1)).getGroupMembers(eq(1L));
    }

    @Test
    void testUpdateGroupMembership() {
        String token = "Bearer token";
        when(groupMembershipService.updateGroupMembership(eq(1L), any(GroupMembershipUpdateDTO.class), eq(token)))
                .thenReturn(groupMembership);

        ResponseEntity<GroupMembership> response = groupMembershipController.updateGroupMembership(1L, groupMembershipUpdateDTO, token);

        assertEquals(200, response.getStatusCodeValue());
        verify(groupMembershipService, times(1)).updateGroupMembership(eq(1L), any(GroupMembershipUpdateDTO.class), eq(token));
    }

    @Test
    void testUpdateGroupMembershipNotFound() {
        String token = "Bearer token";
        when(groupMembershipService.updateGroupMembership(eq(1L), any(GroupMembershipUpdateDTO.class), eq(token)))
                .thenReturn(null);

        ResponseEntity<GroupMembership> response = groupMembershipController.updateGroupMembership(1L, groupMembershipUpdateDTO, token);

        assertEquals(404, response.getStatusCodeValue());
        verify(groupMembershipService, times(1)).updateGroupMembership(eq(1L), any(GroupMembershipUpdateDTO.class), eq(token));
    }
}
