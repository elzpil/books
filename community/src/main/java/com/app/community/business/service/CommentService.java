package com.app.community.business.service;

import com.app.community.model.Comment;

import java.util.List;

public interface CommentService {
    Comment addComment(Comment comment, String token);

    List<Comment> getComments(Long discussionId);
    void deleteComment(Long commentId, String token);
}
