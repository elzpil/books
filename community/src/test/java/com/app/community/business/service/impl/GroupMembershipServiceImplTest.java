package com.app.community.business.service.impl;

import com.app.community.auth.util.JwtTokenUtil;
import com.app.community.business.mapper.GroupMembershipMapper;
import com.app.community.business.repository.GroupMembershipRepository;
import com.app.community.business.repository.model.GroupMembershipDAO;
import com.app.community.business.service.GroupMembershipService;
import com.app.community.dto.GroupMembershipUpdateDTO;
import com.app.community.exception.ResourceNotFoundException;
import com.app.community.exception.UnauthorizedException;
import com.app.community.model.GroupMembership;
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
class GroupMembershipServiceImplTest {

    @Mock
    private GroupMembershipRepository groupMembershipRepository;

    @Mock
    private GroupMembershipMapper groupMembershipMapper;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private GroupMembershipServiceImpl groupMembershipService;

    private GroupMembership groupMembership;
    private GroupMembershipDAO groupMembershipDAO;

    @BeforeEach
    void setUp() {
        groupMembership = new GroupMembership();
        groupMembership.setId(1L);
        groupMembership.setUserId(100L);
        groupMembership.setGroupId(1L);
        groupMembership.setRole("member");
        groupMembership.setJoinedAt(LocalDateTime.now());

        groupMembershipDAO = new GroupMembershipDAO();
        groupMembershipDAO.setId(1L);
        groupMembershipDAO.setUserId(100L);
        groupMembershipDAO.setGroupId(1L);
        groupMembershipDAO.setRole("member");
        groupMembershipDAO.setJoinedAt(LocalDateTime.now());
    }

    @Test
    void testJoinGroup_Success() {
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(100L);
        when(groupMembershipRepository.findByGroupIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(groupMembershipMapper.groupMembershipToGroupMembershipDAO(any(GroupMembership.class))).thenReturn(groupMembershipDAO);
        when(groupMembershipRepository.save(any(GroupMembershipDAO.class))).thenReturn(groupMembershipDAO);
        when(groupMembershipMapper.groupMembershipDAOToGroupMembership(any(GroupMembershipDAO.class))).thenReturn(groupMembership);

        GroupMembership result = groupMembershipService.joinGroup(1L, groupMembership, "Bearer token");

        assertNotNull(result);
        assertEquals(groupMembership.getUserId(), result.getUserId());
        assertEquals(groupMembership.getGroupId(), result.getGroupId());
        assertEquals(groupMembership.getRole(), result.getRole());
        verify(groupMembershipRepository, times(1)).save(any(GroupMembershipDAO.class));
    }

    @Test
    void testJoinGroup_AlreadyMember() {
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(100L);
        when(groupMembershipRepository.findByGroupIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(groupMembershipDAO));
        when(groupMembershipMapper.groupMembershipDAOToGroupMembership(any(GroupMembershipDAO.class))).thenReturn(groupMembership);

        GroupMembership result = groupMembershipService.joinGroup(1L, groupMembership, "Bearer token");

        assertNotNull(result);
        assertEquals(groupMembership.getUserId(), result.getUserId());
        verify(groupMembershipRepository, never()).save(any(GroupMembershipDAO.class));
    }

    @Test
    void testLeaveGroup_Success() {
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(100L);
        when(groupMembershipRepository.findByGroupIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(groupMembershipDAO));

        groupMembershipService.leaveGroup(1L, "Bearer token");

        verify(groupMembershipRepository, times(1)).deleteByGroupIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void testLeaveGroup_Unauthorized() {
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(200L);
        when(groupMembershipRepository.findByGroupIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(groupMembershipDAO));

        assertThrows(UnauthorizedException.class, () -> groupMembershipService.leaveGroup(1L, "Bearer token"));
        verify(groupMembershipRepository, never()).deleteByGroupIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void testLeaveGroup_NotFound() {
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(100L);
        when(groupMembershipRepository.findByGroupIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> groupMembershipService.leaveGroup(1L, "Bearer token"));
        verify(groupMembershipRepository, never()).deleteByGroupIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void testGetGroupMembers_Success() {
        when(groupMembershipRepository.findByGroupId(anyLong())).thenReturn(List.of(groupMembershipDAO));
        when(groupMembershipMapper.groupMembershipDAOToGroupMembership(any(GroupMembershipDAO.class))).thenReturn(groupMembership);

        List<GroupMembership> result = groupMembershipService.getGroupMembers(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(groupMembershipRepository, times(1)).findByGroupId(anyLong());
    }

    @Test
    void testUpdateGroupMembership_Success() {
        GroupMembershipUpdateDTO updateDTO = new GroupMembershipUpdateDTO();
        updateDTO.setRole("moderator");

        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(100L);
        when(groupMembershipRepository.findByGroupIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(groupMembershipDAO));
        when(groupMembershipRepository.save(any(GroupMembershipDAO.class))).thenReturn(groupMembershipDAO);
        when(groupMembershipMapper.groupMembershipDAOToGroupMembership(any(GroupMembershipDAO.class))).thenReturn(groupMembership);

        GroupMembership result = groupMembershipService.updateGroupMembership(1L, updateDTO, "Bearer token");

        assertNotNull(result);
        assertEquals("moderator", groupMembershipDAO.getRole());
        verify(groupMembershipRepository, times(1)).save(any(GroupMembershipDAO.class));
    }

    @Test
    void testUpdateGroupMembership_Unauthorized() {
        GroupMembershipUpdateDTO updateDTO = new GroupMembershipUpdateDTO();
        updateDTO.setRole("moderator");

        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(200L);
        when(groupMembershipRepository.findByGroupIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(groupMembershipDAO));

        assertThrows(UnauthorizedException.class, () -> groupMembershipService.updateGroupMembership(1L, updateDTO, "Bearer token"));
        verify(groupMembershipRepository, never()).save(any(GroupMembershipDAO.class));
    }

    @Test
    void testUpdateGroupMembership_NotFound() {
        GroupMembershipUpdateDTO updateDTO = new GroupMembershipUpdateDTO();
        updateDTO.setRole("moderator");

        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(100L);
        when(groupMembershipRepository.findByGroupIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> groupMembershipService.updateGroupMembership(1L, updateDTO, "Bearer token"));
        verify(groupMembershipRepository, never()).save(any(GroupMembershipDAO.class));
    }
}
