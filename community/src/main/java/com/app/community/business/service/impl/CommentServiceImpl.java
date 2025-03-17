package com.app.community.business.service.impl;

import com.app.community.business.mapper.CommentMapper;
import com.app.community.business.repository.CommentRepository;
import com.app.community.business.repository.model.ChallengeDAO;
import com.app.community.business.repository.model.CommentDAO;
import com.app.community.business.service.CommentService;
import com.app.community.model.Comment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    private final UserServiceClient userServiceClient;

    public CommentServiceImpl(CommentRepository commentRepository, CommentMapper commentMapper, UserServiceClient userServiceClient) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.userServiceClient = userServiceClient;
    }

    @Override
    public Comment addComment(Comment comment) {

        if (!userServiceClient.doesUserExist(comment.getUserId())) {
            throw new IllegalArgumentException("User with ID " + comment.getUserId() + " does not exist");
        }

        CommentDAO commentDAO = commentRepository.save(commentMapper.commentToCommentDAO(comment));
        commentDAO.setCreatedAt(LocalDateTime.now());
        log.info("Saving new comment: {}", commentDAO);
        return commentMapper.commentDAOToComment(commentDAO);
    }

    @Override
    public List<Comment> getComments(Long discussionId) {
        List<CommentDAO> commentDAOs = commentRepository.findByDiscussionId(discussionId);
        log.info("Getting all comments");
        return commentDAOs.stream()
                .map(commentMapper::commentDAOToComment)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteComment(Long commentId) {
        log.info("Deleting a comment");
        commentRepository.deleteById(commentId);
    }
}
