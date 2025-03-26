package com.app.community.controller;

import com.app.community.business.service.DiscussionService;
import com.app.community.business.service.impl.UserServiceClient;
import com.app.community.model.Discussion;
import com.app.community.dto.DiscussionUpdateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DiscussionControllerTest {

    @Mock
    private DiscussionService discussionService;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private DiscussionController discussionController;

    private Discussion discussion;
    private DiscussionUpdateDTO discussionUpdateDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        discussion = new Discussion();
        discussion.setDiscussionId(1L);
        discussion.setBookId(1L);
        discussion.setGroupId(1L);
        discussion.setChallengeId(1L);
        discussion.setUserId(100L);
        discussion.setTitle("Test Discussion");
        discussion.setContent("Test content for the discussion");
        discussion.setCreatedAt(LocalDateTime.now());

        discussionUpdateDTO = new DiscussionUpdateDTO();
        discussionUpdateDTO.setBookId(1L);
        discussionUpdateDTO.setGroupId(1L);
        discussionUpdateDTO.setChallengeId(1L);
        discussionUpdateDTO.setTitle("Updated Test Discussion");
        discussionUpdateDTO.setContent("Updated content for the discussion");
    }

    @Test
    void testCreateDiscussion() {
        String token = "Bearer token";

        when(discussionService.createDiscussion(any(Discussion.class), eq(token))).thenReturn(discussion);

        ResponseEntity<Discussion> response = discussionController.createDiscussion(discussion, token);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(discussion, response.getBody());
        verify(discussionService, times(1)).createDiscussion(any(Discussion.class), eq(token));
    }

    @Test
    void testGetDiscussions() {
        String token = "Bearer token";

        Discussion discussion1 = new Discussion();
        discussion1.setDiscussionId(1L);
        discussion1.setTitle("Test Discussion 1");

        Discussion discussion2 = new Discussion();
        discussion2.setDiscussionId(2L);
        discussion2.setTitle("Test Discussion 2");

        when(discussionService.getDiscussions(eq(token), anyLong(), anyLong(), anyLong())).thenReturn(List.of(discussion1, discussion2));

        ResponseEntity<List<Discussion>> response = discussionController.getDiscussions(token, null, null, null);

        assertEquals(200, response.getStatusCodeValue());
        verify(discussionService, times(1)).getDiscussions(eq(token), eq(null), eq(null), eq(null));
    }

    @Test
    void testGetDiscussion() {
        Long discussionId = 1L;

        when(discussionService.getDiscussion(discussionId)).thenReturn(discussion);

        ResponseEntity<Discussion> response = discussionController.getDiscussion(discussionId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(discussion, response.getBody());
        verify(discussionService, times(1)).getDiscussion(discussionId);
    }

    @Test
    void testUpdateDiscussion() {
        Long discussionId = 1L;
        String token = "Bearer token";

        when(discussionService.updateDiscussion(eq(discussionId), any(DiscussionUpdateDTO.class), eq(token))).thenReturn(discussion);

        ResponseEntity<Discussion> response = discussionController.updateDiscussion(discussionId, discussionUpdateDTO, token);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(discussion, response.getBody());
        verify(discussionService, times(1)).updateDiscussion(eq(discussionId), any(DiscussionUpdateDTO.class), eq(token));
    }

    @Test
    void testDeleteDiscussion() {
        Long discussionId = 1L;
        String token = "Bearer token";

        doNothing().when(discussionService).deleteDiscussion(discussionId, token);

        ResponseEntity<Void> response = discussionController.deleteDiscussion(discussionId, token);

        assertEquals(204, response.getStatusCodeValue());
        verify(discussionService, times(1)).deleteDiscussion(discussionId, token);
    }
}
