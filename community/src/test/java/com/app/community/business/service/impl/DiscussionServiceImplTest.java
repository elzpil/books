package com.app.community.business.service.impl;

import com.app.community.auth.util.JwtTokenUtil;
import com.app.community.business.mapper.DiscussionMapper;
import com.app.community.business.repository.DiscussionRepository;
import com.app.community.business.repository.model.DiscussionDAO;
import com.app.community.business.service.DiscussionService;
import com.app.community.dto.DiscussionUpdateDTO;
import com.app.community.exception.ResourceNotFoundException;
import com.app.community.exception.UnauthorizedException;
import com.app.community.model.Discussion;
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
class DiscussionServiceImplTest {

    @Mock
    private DiscussionRepository discussionRepository;

    @Mock
    private DiscussionMapper discussionMapper;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private BookServiceClient bookServiceClient;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private DiscussionServiceImpl discussionService;

    private Discussion discussion;
    private DiscussionDAO discussionDAO;

    @BeforeEach
    void setUp() {
        discussion = new Discussion();
        discussion.setDiscussionId(1L);
        discussion.setUserId(100L);
        discussion.setTitle("Test Discussion");
        discussion.setContent("Discussion Content");
        discussion.setCreatedAt(LocalDateTime.now());

        discussionDAO = new DiscussionDAO();
        discussionDAO.setDiscussionId(1L);
        discussionDAO.setUserId(100L);
        discussionDAO.setTitle("Test Discussion");
        discussionDAO.setContent("Discussion Content");
        discussionDAO.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateDiscussion_Success() {
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(100L);
        when(discussionMapper.discussionToDiscussionDAO(any(Discussion.class))).thenReturn(discussionDAO);
        when(discussionRepository.save(any(DiscussionDAO.class))).thenReturn(discussionDAO);
        when(discussionMapper.discussionDAOToDiscussion(any(DiscussionDAO.class))).thenReturn(discussion);

        Discussion result = discussionService.createDiscussion(discussion, "Bearer token");

        assertNotNull(result);
        assertEquals(discussion.getDiscussionId(), result.getDiscussionId());
        assertEquals(discussion.getTitle(), result.getTitle());
        verify(discussionRepository, times(1)).save(any(DiscussionDAO.class));
    }

    @Test
    void testGetDiscussions_Success() {
        when(jwtTokenUtil.extractRole(anyString())).thenReturn("USER");
        when(discussionRepository.findByBookId(anyLong())).thenReturn(List.of(discussionDAO));
        when(discussionMapper.discussionDAOToDiscussion(any(DiscussionDAO.class))).thenReturn(discussion);

        List<Discussion> discussions = discussionService.getDiscussions("Bearer token", null, 1L, null);

        assertFalse(discussions.isEmpty());
        assertEquals(1, discussions.size());
        verify(discussionRepository, times(1)).findByBookId(anyLong());
    }

    @Test
    void testGetDiscussion_Success() {
        when(discussionRepository.findById(anyLong())).thenReturn(Optional.of(discussionDAO));
        when(discussionMapper.discussionDAOToDiscussion(any(DiscussionDAO.class))).thenReturn(discussion);

        Discussion result = discussionService.getDiscussion(1L);

        assertNotNull(result);
        assertEquals(discussion.getDiscussionId(), result.getDiscussionId());
        verify(discussionRepository, times(1)).findById(anyLong());
    }

    @Test
    void testUpdateDiscussion_Success() {
        DiscussionUpdateDTO updateDTO = new DiscussionUpdateDTO();
        updateDTO.setTitle("Updated Title");

        when(discussionRepository.findById(anyLong())).thenReturn(Optional.of(discussionDAO));
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(100L);
        when(discussionRepository.save(any(DiscussionDAO.class))).thenReturn(discussionDAO);
        when(discussionMapper.discussionDAOToDiscussion(any(DiscussionDAO.class))).thenReturn(discussion);

        Discussion result = discussionService.updateDiscussion(1L, updateDTO, "Bearer token");

        assertNotNull(result);
        assertEquals("Updated Title", discussionDAO.getTitle());
        verify(discussionRepository, times(1)).save(any(DiscussionDAO.class));
    }

    @Test
    void testUpdateDiscussion_Unauthorized() {
        DiscussionUpdateDTO updateDTO = new DiscussionUpdateDTO();
        updateDTO.setTitle("Updated Title");

        when(discussionRepository.findById(anyLong())).thenReturn(Optional.of(discussionDAO));
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(200L);

        assertThrows(UnauthorizedException.class, () -> discussionService.updateDiscussion(1L, updateDTO, "Bearer token"));
        verify(discussionRepository, never()).save(any(DiscussionDAO.class));
    }

    @Test
    void testDeleteDiscussion_Success() {
        when(discussionRepository.findById(anyLong())).thenReturn(Optional.of(discussionDAO));
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(100L);
        when(jwtTokenUtil.extractRole(anyString())).thenReturn("USER");

        discussionService.deleteDiscussion(1L, "Bearer token");

        verify(discussionRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteDiscussion_Unauthorized() {
        when(discussionRepository.findById(anyLong())).thenReturn(Optional.of(discussionDAO));
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(200L);
        when(jwtTokenUtil.extractRole(anyString())).thenReturn("USER");

        assertThrows(UnauthorizedException.class, () -> discussionService.deleteDiscussion(1L, "Bearer token"));
        verify(discussionRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteDiscussion_NotFound() {
        when(discussionRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> discussionService.deleteDiscussion(1L, "Bearer token"));
        verify(discussionRepository, never()).deleteById(anyLong());
    }
    @Test
    void testCreateDiscussion_BookDoesNotExist_ShouldThrowException() {
        discussion.setBookId(99L);

        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(100L);
        when(bookServiceClient.doesBookExist(eq(99L), anyString())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                discussionService.createDiscussion(discussion, "Bearer token")
        );

        assertEquals("Book with ID 99 does not exist.", exception.getMessage());
        verify(bookServiceClient, times(1)).doesBookExist(eq(99L), anyString());
    }

    @Test
    void testGetDiscussions_NoParams_NonAdmin_ShouldThrowUnauthorized() {
        when(jwtTokenUtil.extractRole(anyString())).thenReturn("USER");

        assertThrows(UnauthorizedException.class, () ->
                discussionService.getDiscussions("Bearer token", null, null, null)
        );
    }

    @Test
    void testGetDiscussions_NoParams_Admin_ShouldReturnAll() {
        when(jwtTokenUtil.extractRole(anyString())).thenReturn("ADMIN");
        when(discussionRepository.findAll()).thenReturn(List.of(discussionDAO));
        when(discussionMapper.discussionDAOToDiscussion(any(DiscussionDAO.class))).thenReturn(discussion);

        List<Discussion> result = discussionService.getDiscussions("Bearer token", null, null, null);

        assertEquals(1, result.size());
        verify(discussionRepository, times(1)).findAll();
    }

    @Test
    void testUpdateDiscussion_DiscussionNotFound_ShouldReturnNull() {
        when(discussionRepository.findById(anyLong())).thenReturn(Optional.empty());

        Discussion result = discussionService.updateDiscussion(1L, new DiscussionUpdateDTO(), "Bearer token");

        assertNull(result);
        verify(discussionRepository, never()).save(any());
    }

}
