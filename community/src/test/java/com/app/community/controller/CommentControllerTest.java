package com.app.community.controller;

import com.app.community.business.service.CommentService;
import com.app.community.model.Comment;
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
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private Comment comment;

    @BeforeEach
    void setUp() {
        comment = new Comment();
        comment.setCommentId(1L);
        comment.setDiscussionId(10L);
        comment.setUserId(100L);
        comment.setContent("This is a test comment.");
        comment.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void addComment_ShouldReturnComment() {
        when(commentService.addComment(10L, 100L, "This is a test comment.")).thenReturn(comment);

        ResponseEntity<Comment> response = commentController.addComment(10L, 100L, "This is a test comment.");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(comment);
        verify(commentService).addComment(10L, 100L, "This is a test comment.");
    }

    @Test
    void getComments_ShouldReturnListOfComments() {
        when(commentService.getComments(10L)).thenReturn(List.of(comment));

        ResponseEntity<List<Comment>> response = commentController.getComments(10L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull().hasSize(1);
        verify(commentService).getComments(10L);
    }

    @Test
    void deleteComment_ShouldReturnNoContent() {
        doNothing().when(commentService).deleteComment(1L);

        ResponseEntity<Void> response = commentController.deleteComment(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        verify(commentService).deleteComment(1L);
    }
}
