package com.app.community.controller;

import com.app.community.business.service.CommentService;
import com.app.community.model.Comment;
import com.app.community.dto.CommentUpdateDTO;
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

class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddComment() {
        String token = "Bearer token";

        Comment comment = new Comment();
        comment.setCommentId(1L);
        comment.setDiscussionId(1L);
        comment.setUserId(100L);
        comment.setContent("This is a test comment.");
        comment.setCreatedAt(LocalDateTime.now());

        when(commentService.addComment(any(Comment.class), eq(token))).thenReturn(comment);

        ResponseEntity<Comment> response = commentController.addComment(comment, token);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(comment, response.getBody());
        verify(commentService, times(1)).addComment(any(Comment.class), eq(token));
    }

    @Test
    void testGetComments() {
        Long discussionId = 1L;

        Comment comment1 = new Comment();
        comment1.setCommentId(1L);
        comment1.setDiscussionId(discussionId);
        comment1.setUserId(100L);
        comment1.setContent("First comment");
        comment1.setCreatedAt(LocalDateTime.now());

        Comment comment2 = new Comment();
        comment2.setCommentId(2L);
        comment2.setDiscussionId(discussionId);
        comment2.setUserId(101L);
        comment2.setContent("Second comment");
        comment2.setCreatedAt(LocalDateTime.now());

        when(commentService.getComments(discussionId)).thenReturn(List.of(comment1, comment2));

        ResponseEntity<List<Comment>> response = commentController.getComments(discussionId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(commentService, times(1)).getComments(discussionId);
    }

    @Test
    void testDeleteComment() {
        Long commentId = 1L;
        String token = "Bearer token";

        doNothing().when(commentService).deleteComment(commentId, token);

        ResponseEntity<Void> response = commentController.deleteComment(commentId, token);

        assertEquals(204, response.getStatusCodeValue());
        verify(commentService, times(1)).deleteComment(commentId, token);
    }
}
