package com.app.community.controller;

import com.app.community.business.service.DiscussionService;
import com.app.community.business.service.impl.UserServiceClient;
import com.app.community.model.Discussion;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/discussions")
public class DiscussionController {

    private final DiscussionService discussionService;
    private final UserServiceClient userServiceClient;

    public DiscussionController(DiscussionService discussionService, UserServiceClient userServiceClient) {
        this.discussionService = discussionService;
        this.userServiceClient = userServiceClient;
    }
    @PostMapping
    public ResponseEntity<Discussion> createDiscussion(@RequestBody @Valid Discussion discussion) {

        if (!userServiceClient.doesUserExist(discussion.getUserId())) {
            log.error("User with ID {} does not exist", discussion.getUserId());
            return ResponseEntity.badRequest().build();
        }

        log.info("Creating discussion with title: {}", discussion.getTitle());
        Discussion createdDiscussion = discussionService.createDiscussion(discussion);
        return ResponseEntity.ok(createdDiscussion);
    }

    @GetMapping
    public ResponseEntity<List<Discussion>> getDiscussions(@RequestParam(required = false) Long groupId,
                                                           @RequestParam(required = false) Long bookId,
                                                           @RequestParam(required = false) Long challengeId) {
        List<Discussion> discussions = discussionService.getDiscussions(groupId, bookId, challengeId);
        log.info("Getting discusions");
        return ResponseEntity.ok(discussions);
    }

    @GetMapping("/{discussionId}")
    public ResponseEntity<Discussion> getDiscussion(@PathVariable Long discussionId) {
        Discussion discussion = discussionService.getDiscussion(discussionId);
        log.info("Getting discusion with id {}", discussionId);
        return discussion != null ? ResponseEntity.ok(discussion) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{discussionId}")
    public ResponseEntity<Discussion> updateDiscussion(@PathVariable Long discussionId,
                                                       @RequestParam String title,
                                                       @RequestParam String content) {
        Discussion updatedDiscussion = discussionService.updateDiscussion(discussionId, title, content);
        log.info("Updating discusion with id {}", discussionId);
        return updatedDiscussion != null ? ResponseEntity.ok(updatedDiscussion) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{discussionId}")
    public ResponseEntity<Void> deleteDiscussion(@PathVariable Long discussionId) {
        log.info("Deleting discusion with id {}", discussionId);
        discussionService.deleteDiscussion(discussionId);
        return ResponseEntity.noContent().build();
    }
}
