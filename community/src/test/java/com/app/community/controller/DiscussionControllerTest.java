package com.app.community.controller;

import com.app.community.business.service.DiscussionService;
import com.app.community.model.Discussion;
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
class DiscussionControllerTest {

    @Mock
    private DiscussionService discussionService;

    @InjectMocks
    private DiscussionController discussionController;

    private Discussion discussion;

    @BeforeEach
    void setUp() {
        discussion = new Discussion();
        discussion.setDiscussionId(1L);
        discussion.setUserId(100L);
        discussion.setTitle("Test Discussion");
        discussion.setContent("This is a test discussion.");
        discussion.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createDiscussion_ShouldReturnDiscussion() {
        when(discussionService.createDiscussion(null, null, null, 100L, "Test Discussion", "This is a test discussion."))
                .thenReturn(discussion);

        ResponseEntity<Discussion> response = discussionController.createDiscussion(new Discussion(null, null, null, 100L, "Test Discussion", "This is a test discussion."));

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(discussion);
        verify(discussionService).createDiscussion(null, null, null, 100L, "Test Discussion", "This is a test discussion.");
    }

    @Test
    void getDiscussions_ShouldReturnListOfDiscussions() {
        when(discussionService.getDiscussions(null, null, null)).thenReturn(List.of(discussion));

        ResponseEntity<List<Discussion>> response = discussionController.getDiscussions(null, null, null);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull().hasSize(1);
        verify(discussionService).getDiscussions(null, null, null);
    }

    @Test
    void getDiscussion_ShouldReturnDiscussion() {
        when(discussionService.getDiscussion(1L)).thenReturn(discussion);

        ResponseEntity<Discussion> response = discussionController.getDiscussion(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(discussion);
        verify(discussionService).getDiscussion(1L);
    }

    @Test
    void updateDiscussion_ShouldReturnUpdatedDiscussion() {
        Discussion updatedDiscussion = new Discussion();
        updatedDiscussion.setDiscussionId(1L);
        updatedDiscussion.setUserId(100L);
        updatedDiscussion.setTitle("Updated Title");
        updatedDiscussion.setContent("Updated Content");
        updatedDiscussion.setCreatedAt(LocalDateTime.now());

        when(discussionService.updateDiscussion(1L, "Updated Title", "Updated Content"))
                .thenReturn(updatedDiscussion);

        ResponseEntity<Discussion> response = discussionController.updateDiscussion(1L, "Updated Title", "Updated Content");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(updatedDiscussion);
        verify(discussionService).updateDiscussion(1L, "Updated Title", "Updated Content");
    }

    @Test
    void deleteDiscussion_ShouldReturnNoContent() {
        doNothing().when(discussionService).deleteDiscussion(1L);

        ResponseEntity<Void> response = discussionController.deleteDiscussion(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        verify(discussionService).deleteDiscussion(1L);
    }
}
