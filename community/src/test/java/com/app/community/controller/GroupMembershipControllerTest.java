package com.app.community.controller;

import com.app.community.business.service.GroupMembershipService;
import com.app.community.model.GroupMembership;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupMembershipControllerTest {

    @Mock
    private GroupMembershipService groupMembershipService;

    @InjectMocks
    private GroupMembershipController groupMembershipController;

    private GroupMembership membership;

    @BeforeEach
    void setUp() {
        membership = new GroupMembership();
        membership.setId(1L);
        membership.setUserId(100L);
        membership.setGroupId(10L);
        membership.setRole("member");
        membership.setJoinedAt(LocalDateTime.now());
    }

    @Test
    void joinGroup_ShouldReturnMembership() {
        when(groupMembershipService.joinGroup(10L, 100L, "member")).thenReturn(membership);

        ResponseEntity<GroupMembership> response = groupMembershipController.joinGroup(10L, 100L, "member");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(membership);
        verify(groupMembershipService).joinGroup(10L, 100L, "member");
    }

    @Test
    void leaveGroup_ShouldReturnNoContent() {
        doNothing().when(groupMembershipService).leaveGroup(10L, 100L);

        ResponseEntity<Void> response = groupMembershipController.leaveGroup(10L, 100L);

        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        verify(groupMembershipService).leaveGroup(10L, 100L);
    }

    @Test
    void getGroupMembers_ShouldReturnListOfMembers() {
        when(groupMembershipService.getGroupMembers(10L)).thenReturn(List.of(membership));

        ResponseEntity<List<GroupMembership>> response = groupMembershipController.getGroupMembers(10L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull().hasSize(1);
        verify(groupMembershipService).getGroupMembers(10L);
    }
}
