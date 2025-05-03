package com.app.community.business.service.impl;

import com.app.community.auth.util.JwtTokenUtil;
import com.app.community.business.mapper.GroupMembershipMapper;
import com.app.community.business.repository.GroupMembershipRepository;
import com.app.community.business.repository.model.GroupDAO;
import com.app.community.business.repository.model.GroupMembershipDAO;
import com.app.community.business.service.GroupMembershipService;
import com.app.community.dto.GroupMembershipUpdateDTO;
import com.app.community.exception.ResourceNotFoundException;
import com.app.community.exception.UnauthorizedException;
import com.app.community.model.Group;
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


    @Test
    void testFindByGroupIdAndUserId_Found() {
        when(groupMembershipRepository.findByGroupIdAndUserId(1L, 100L)).thenReturn(Optional.of(groupMembershipDAO));
        when(groupMembershipMapper.groupMembershipDAOToGroupMembership(groupMembershipDAO)).thenReturn(groupMembership);

        Optional<GroupMembership> result = groupMembershipService.findByGroupIdAndUserId(1L, 100L);

        assertTrue(result.isPresent());
        assertEquals(100L, result.get().getUserId());
    }

    @Test
    void testFindByGroupIdAndUserId_NotFound() {
        when(groupMembershipRepository.findByGroupIdAndUserId(1L, 100L)).thenReturn(Optional.empty());

        Optional<GroupMembership> result = groupMembershipService.findByGroupIdAndUserId(1L, 100L);

        assertFalse(result.isPresent());
    }

    @Test
    void testRemoveMember_Success() {
        GroupMembershipDAO adminDAO = new GroupMembershipDAO();
        adminDAO.setUserId(200L);
        adminDAO.setGroupId(1L);
        adminDAO.setRole("admin");

        GroupMembershipDAO memberDAO = new GroupMembershipDAO();
        memberDAO.setUserId(100L);
        memberDAO.setGroupId(1L);
        memberDAO.setRole("member");

        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(200L);
        when(groupMembershipRepository.findByGroupIdAndUserId(1L, 200L)).thenReturn(Optional.of(adminDAO));
        when(groupMembershipRepository.findByGroupIdAndUserId(1L, 100L)).thenReturn(Optional.of(memberDAO));

        groupMembershipService.removeMember(1L, 100L, "Bearer token");

        verify(groupMembershipRepository).deleteByGroupIdAndUserId(1L, 100L);
    }

    @Test
    void testRemoveMember_Unauthorized() {
        GroupMembershipDAO nonAdminDAO = new GroupMembershipDAO();
        nonAdminDAO.setUserId(200L);
        nonAdminDAO.setGroupId(1L);
        nonAdminDAO.setRole("member");

        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(200L);
        when(groupMembershipRepository.findByGroupIdAndUserId(1L, 200L)).thenReturn(Optional.of(nonAdminDAO));

        assertThrows(UnauthorizedException.class, () ->
                groupMembershipService.removeMember(1L, 100L, "Bearer token"));

        verify(groupMembershipRepository, never()).deleteByGroupIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void testRemoveMember_TargetNotFound() {
        GroupMembershipDAO adminDAO = new GroupMembershipDAO();
        adminDAO.setUserId(200L);
        adminDAO.setGroupId(1L);
        adminDAO.setRole("admin");

        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(200L);
        when(groupMembershipRepository.findByGroupIdAndUserId(1L, 200L)).thenReturn(Optional.of(adminDAO));
        when(groupMembershipRepository.findByGroupIdAndUserId(1L, 100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                groupMembershipService.removeMember(1L, 100L, "Bearer token"));

        verify(groupMembershipRepository, never()).deleteByGroupIdAndUserId(anyLong(), anyLong());
    }


}
