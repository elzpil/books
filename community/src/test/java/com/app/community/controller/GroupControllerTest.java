package com.app.community.controller;

import com.app.community.business.service.GroupService;
import com.app.community.dto.GroupUpdateDTO;
import com.app.community.model.Group;
import com.app.community.model.PrivacySetting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
        group.setName("Book Club");
        group.setDescription("A group for book enthusiasts");
        group.setCreatorId(100L);
        group.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateGroup() {
        when(groupService.createGroup(any(Group.class), any(String.class))).thenReturn(group);
        ResponseEntity<Group> response = groupController.createGroup(group, "Bearer token");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(group, response.getBody());
    }

    @Test
    void testGetAllGroups() {
        when(groupService.getAllGroups()).thenReturn(List.of(group));
        ResponseEntity<List<Group>> response = groupController.getAllGroups();
        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void testGetGroupById_Found() {
        when(groupService.getGroupById(1L)).thenReturn(Optional.of(group));
        ResponseEntity<Group> response = groupController.getGroupById(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(group, response.getBody());
    }

    @Test
    void testGetGroupById_NotFound() {
        when(groupService.getGroupById(1L)).thenReturn(Optional.empty());
        ResponseEntity<Group> response = groupController.getGroupById(1L);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testUpdateGroup() {
        GroupUpdateDTO updateDTO = new GroupUpdateDTO();
        updateDTO.setName("Updated Book Club");
        updateDTO.setDescription("An updated description of the book club");
        updateDTO.setPrivacySetting(PrivacySetting.PUBLIC);

        when(groupService.updateGroup(eq(1L), any(GroupUpdateDTO.class), any(String.class))).thenReturn(group);
        ResponseEntity<Group> response = groupController.updateGroup(1L, updateDTO, "Bearer token");
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testDeleteGroup() {
        doNothing().when(groupService).deleteGroup(1L, "Bearer token");
        ResponseEntity<Void> response = groupController.deleteGroup(1L, "Bearer token");
        assertEquals(204, response.getStatusCodeValue());
    }


}
