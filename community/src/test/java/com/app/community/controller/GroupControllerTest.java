package com.app.community.controller;

import com.app.community.business.service.GroupService;
import com.app.community.model.Group;
import com.app.community.model.PrivacySetting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupControllerTest {

    @Mock
    private GroupService groupService;

    @InjectMocks
    private GroupController groupController;

    private Group group;

    @BeforeEach
    void setUp() {
        group = new Group();
        group.setId(1L);
        group.setName("Tech Enthusiasts");
        group.setDescription("A group for tech lovers");
        group.setPrivacySetting(PrivacySetting.PUBLIC);
        group.setCreatorId(100L);
        group.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createGroup_ShouldReturnCreatedGroup() {
        when(groupService.createGroup(group)).thenReturn(group);

        ResponseEntity<Group> response = groupController.createGroup(group);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(group);
        verify(groupService).createGroup(group);
    }

    @Test
    void getAllGroups_ShouldReturnListOfGroups() {
        when(groupService.getAllGroups()).thenReturn(List.of(group));

        ResponseEntity<List<Group>> response = groupController.getAllGroups();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull().hasSize(1);
        verify(groupService).getAllGroups();
    }

    @Test
    void getGroupById_ShouldReturnGroup_WhenGroupExists() {
        when(groupService.getGroupById(1L)).thenReturn(Optional.of(group));

        ResponseEntity<Group> response = groupController.getGroupById(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(group);
        verify(groupService).getGroupById(1L);
    }

    @Test
    void getGroupById_ShouldReturnNotFound_WhenGroupDoesNotExist() {
        when(groupService.getGroupById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Group> response = groupController.getGroupById(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        verify(groupService).getGroupById(1L);
    }

    @Test
    void updateGroup_ShouldReturnUpdatedGroup_WhenGroupExists() {
        Group updatedGroup = new Group(1L, "Updated Name", "Updated Description", PrivacySetting.PRIVATE, 100L, LocalDateTime.now());
        when(groupService.updateGroup(1L, updatedGroup)).thenReturn(updatedGroup);

        ResponseEntity<Group> response = groupController.updateGroup(1L, updatedGroup);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(updatedGroup);
        verify(groupService).updateGroup(1L, updatedGroup);
    }

    @Test
    void updateGroup_ShouldReturnNotFound_WhenGroupDoesNotExist() {
        when(groupService.updateGroup(1L, group)).thenReturn(null);

        ResponseEntity<Group> response = groupController.updateGroup(1L, group);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        verify(groupService).updateGroup(1L, group);
    }

    @Test
    void deleteGroup_ShouldReturnNoContent() {
        doNothing().when(groupService).deleteGroup(1L);

        ResponseEntity<Void> response = groupController.deleteGroup(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        verify(groupService).deleteGroup(1L);
    }
}
