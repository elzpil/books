package com.app.community.business.service.impl;

import com.app.community.auth.util.JwtTokenUtil;
import com.app.community.business.mapper.CommentMapper;
import com.app.community.business.repository.CommentRepository;
import com.app.community.business.repository.model.CommentDAO;
import com.app.community.business.service.CommentService;
import com.app.community.exception.ResourceNotFoundException;
import com.app.community.exception.UnauthorizedException;
import com.app.community.model.Comment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserServiceClient userServiceClient;

    public CommentServiceImpl(CommentRepository commentRepository, CommentMapper commentMapper, JwtTokenUtil jwtTokenUtil, UserServiceClient userServiceClient) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userServiceClient = userServiceClient;
    }

    @Override
    public Comment addComment(Comment comment, String token) {
        log.info("Adding new comment for discussion ID: {}", comment.getDiscussionId());

        Long userId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));
        if (!userServiceClient.doesUserExist(userId)) {
            throw new IllegalArgumentException("User with ID " + userId + " does not exist");
        }

        CommentDAO commentDAO = commentMapper.commentToCommentDAO(comment);
        commentDAO.setUserId(userId);
        commentDAO.setCreatedAt(LocalDateTime.now());

        CommentDAO savedComment = commentRepository.save(commentDAO);
        log.info("Successfully saved comment with ID {}", savedComment.getCommentId());

        return commentMapper.commentDAOToComment(savedComment);
    }

    @Override
    public List<Comment> getComments(Long discussionId) {
        log.info("Fetching comments for discussion ID: {}", discussionId);
        List<CommentDAO> commentDAOs = commentRepository.findByDiscussionId(discussionId);
        return commentDAOs.stream()
                .map(commentMapper::commentDAOToComment)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteComment(Long commentId, String token) {
        log.info("Attempting to delete comment with ID: {}", commentId);

        CommentDAO commentDAO = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));

        Long userId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));
        if (!isAuthorized(token, commentDAO.getUserId())) {
            log.warn("Unauthorized attempt to delete comment ID {} by user ID {}", commentId, userId);
            throw new UnauthorizedException("You are not authorized to delete this comment");
        }

        commentRepository.deleteById(commentId);
        log.info("Successfully deleted comment ID {}", commentId);
    }

    private boolean isAuthorized(String token, Long commentOwnerId) {
        Long tokenUserId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));
        String role = jwtTokenUtil.extractRole(token.replace("Bearer ", ""));
        return tokenUserId.equals(commentOwnerId) || "ADMIN".equals(role);
    }
}
