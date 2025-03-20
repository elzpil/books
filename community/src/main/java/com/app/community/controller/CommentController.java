package com.app.community.controller;

import com.app.community.business.service.CommentService;
import com.app.community.model.Comment;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<Comment> addComment(@Valid @RequestBody Comment comment, @RequestHeader("Authorization") String token) {
        log.info("Adding comment for discussion: {}", comment.getDiscussionId());
        Comment savedComment = commentService.addComment(comment, token);
        return ResponseEntity.ok(savedComment);
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getComments(@RequestParam Long discussionId) {
        log.info("Getting comments for discussion: {}", discussionId);
        List<Comment> comments = commentService.getComments(discussionId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId ,@RequestHeader("Authorization") String token) {
        log.info("Deleting comment for discussion: {}", commentId);
        commentService.deleteComment(commentId, token);
        return ResponseEntity.noContent().build();
    }
}
