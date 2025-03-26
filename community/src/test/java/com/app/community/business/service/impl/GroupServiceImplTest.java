package com.app.community.business.service.impl;

import com.app.community.auth.util.JwtTokenUtil;
import com.app.community.business.mapper.GroupMapper;
import com.app.community.business.repository.GroupRepository;
import com.app.community.business.repository.model.GroupDAO;
import com.app.community.business.service.GroupService;
import com.app.community.dto.GroupUpdateDTO;
import com.app.community.exception.ResourceNotFoundException;
import com.app.community.exception.UnauthorizedException;
import com.app.community.model.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupMapper groupMapper;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private GroupServiceImpl groupService;

    private Group group;
    private GroupDAO groupDAO;

    @BeforeEach
    void setUp() {
        group = new Group(1L, "Test Group", "Test Description", null, 100L, LocalDateTime.now());
        groupDAO = new GroupDAO();
        groupDAO.setId(1L);
        groupDAO.setName("Test Group");
        groupDAO.setDescription("Test Description");
        groupDAO.setPrivacySetting(null);
        groupDAO.setCreatorId(100L);
        groupDAO.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateGroup_Success() {
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(100L);
        when(groupMapper.groupToGroupDAO(any(Group.class))).thenReturn(groupDAO);
        when(groupRepository.save(any(GroupDAO.class))).thenReturn(groupDAO);
        when(groupMapper.groupDAOToGroup(any(GroupDAO.class))).thenReturn(group);

        Group result = groupService.createGroup(group, "Bearer token");

        assertNotNull(result);
        assertEquals(group.getId(), result.getId());
        assertEquals(group.getName(), result.getName());
        verify(groupRepository, times(1)).save(any(GroupDAO.class));
    }

    @Test
    void testGetAllGroups_Success() {
        when(groupRepository.findAll()).thenReturn(List.of(groupDAO));
        when(groupMapper.groupDAOToGroup(any(GroupDAO.class))).thenReturn(group);

        List<Group> groups = groupService.getAllGroups();

        assertFalse(groups.isEmpty());
        assertEquals(1, groups.size());
        verify(groupRepository, times(1)).findAll();
    }

    @Test
    void testGetGroupById_Success() {
        when(groupRepository.findById(anyLong())).thenReturn(Optional.of(groupDAO));
        when(groupMapper.groupDAOToGroup(any(GroupDAO.class))).thenReturn(group);

        Optional<Group> result = groupService.getGroupById(1L);

        assertTrue(result.isPresent());
        assertEquals(group.getId(), result.get().getId());
        verify(groupRepository, times(1)).findById(anyLong());
    }

    @Test
    void testUpdateGroup_Success() {
        GroupUpdateDTO updateDTO = new GroupUpdateDTO();
        updateDTO.setName("Updated Name");
        updateDTO.setDescription("Updated Description");

        when(groupRepository.findById(anyLong())).thenReturn(Optional.of(groupDAO));
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(100L);
        when(groupRepository.save(any(GroupDAO.class))).thenReturn(groupDAO);
        when(groupMapper.groupDAOToGroup(any(GroupDAO.class))).thenReturn(group);

        Group result = groupService.updateGroup(1L, updateDTO, "Bearer token");

        assertNotNull(result);
        assertEquals("Updated Name", groupDAO.getName());
        assertEquals("Updated Description", groupDAO.getDescription());
        verify(groupRepository, times(1)).save(any(GroupDAO.class));
    }

    @Test
    void testUpdateGroup_Unauthorized() {
        GroupUpdateDTO updateDTO = new GroupUpdateDTO();
        updateDTO.setName("Updated Name");

        when(groupRepository.findById(anyLong())).thenReturn(Optional.of(groupDAO));
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(200L);

        assertThrows(UnauthorizedException.class, () -> groupService.updateGroup(1L, updateDTO, "Bearer token"));
        verify(groupRepository, never()).save(any(GroupDAO.class));
    }

    @Test
    void testDeleteGroup_Success() {
        when(groupRepository.findById(anyLong())).thenReturn(Optional.of(groupDAO));
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(100L);

        groupService.deleteGroup(1L, "Bearer token");

        verify(groupRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteGroup_Unauthorized() {
        when(groupRepository.findById(anyLong())).thenReturn(Optional.of(groupDAO));
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(200L);

        assertThrows(UnauthorizedException.class, () -> groupService.deleteGroup(1L, "Bearer token"));
        verify(groupRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteGroup_NotFound() {
        when(groupRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> groupService.deleteGroup(1L, "Bearer token"));
        verify(groupRepository, never()).deleteById(anyLong());
    }

    @Test
    void testSearchGroupsByName_Success() {
        when(groupRepository.searchGroupsByName(anyString())).thenReturn(List.of(groupDAO));
        when(groupMapper.groupDAOToGroup(any(GroupDAO.class))).thenReturn(group);

        List<Group> result = groupService.searchGroupsByName("Test");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(groupRepository, times(1)).searchGroupsByName(anyString());
    }
}
