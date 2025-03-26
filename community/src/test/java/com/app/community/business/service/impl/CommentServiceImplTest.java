package com.app.community.business.service.impl;

import com.app.community.auth.util.JwtTokenUtil;
import com.app.community.business.mapper.CommentMapper;
import com.app.community.business.repository.CommentRepository;
import com.app.community.business.repository.model.CommentDAO;

import com.app.community.exception.ResourceNotFoundException;
import com.app.community.exception.UnauthorizedException;
import com.app.community.model.Comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Comment comment;
    private CommentDAO commentDAO;
    private String token;
    private Long userId;
    private Long commentId;
    private Long discussionId;

    @BeforeEach
    void setUp() {
        token = "Bearer valid-token";
        userId = 1L;
        commentId = 100L;
        discussionId = 200L;

        comment = new Comment();
        comment.setCommentId(commentId);
        comment.setDiscussionId(discussionId);
        comment.setUserId(userId);
        comment.setContent("This is a test comment");
        comment.setCreatedAt(LocalDateTime.now());

        commentDAO = new CommentDAO();
        commentDAO.setCommentId(commentId);
        commentDAO.setDiscussionId(discussionId);
        commentDAO.setUserId(userId);
        commentDAO.setContent("This is a test comment");
        commentDAO.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void addComment_ShouldSaveAndReturnComment_WhenUserExists() {
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(userId);
        when(userServiceClient.doesUserExist(userId)).thenReturn(true);
        when(commentMapper.commentToCommentDAO(any(Comment.class))).thenReturn(commentDAO);
        when(commentRepository.save(any(CommentDAO.class))).thenReturn(commentDAO);
        when(commentMapper.commentDAOToComment(any(CommentDAO.class))).thenReturn(comment);

        Comment savedComment = commentService.addComment(comment, token);

        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getCommentId()).isEqualTo(commentId);
        verify(commentRepository, times(1)).save(any(CommentDAO.class));
    }

    @Test
    void addComment_ShouldThrowException_WhenUserDoesNotExist() {
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(userId);
        when(userServiceClient.doesUserExist(userId)).thenReturn(false);

        assertThatThrownBy(() -> commentService.addComment(comment, token))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User with ID " + userId + " does not exist");

        verify(commentRepository, never()).save(any(CommentDAO.class));
    }

    @Test
    void getComments_ShouldReturnListOfComments() {
        when(commentRepository.findByDiscussionId(discussionId)).thenReturn(List.of(commentDAO));
        when(commentMapper.commentDAOToComment(any(CommentDAO.class))).thenReturn(comment);

        List<Comment> comments = commentService.getComments(discussionId);

        assertThat(comments).isNotEmpty();
        assertThat(comments.get(0).getCommentId()).isEqualTo(commentId);
        verify(commentRepository, times(1)).findByDiscussionId(discussionId);
    }

    @Test
    void deleteComment_ShouldDeleteComment_WhenAuthorized() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(commentDAO));
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(userId);
        when(jwtTokenUtil.extractRole(anyString())).thenReturn("USER");

        commentService.deleteComment(commentId, token);

        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @Test
    void deleteComment_ShouldThrowUnauthorizedException_WhenUserIsNotAuthorized() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(commentDAO));
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(2L); // Different user
        when(jwtTokenUtil.extractRole(anyString())).thenReturn("USER");

        assertThatThrownBy(() -> commentService.deleteComment(commentId, token))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("You are not authorized to delete this comment");

        verify(commentRepository, never()).deleteById(anyLong());
    }

    /**
     * Test: Delete Comment (Comment Not Found)
     */
    @Test
    void deleteComment_ShouldThrowResourceNotFoundException_WhenCommentDoesNotExist() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.deleteComment(commentId, token))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Comment");

        verify(commentRepository, never()).deleteById(anyLong());
    }
}
