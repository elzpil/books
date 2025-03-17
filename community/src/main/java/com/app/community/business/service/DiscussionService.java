package com.app.community.business.service;

import com.app.community.model.Discussion;

import java.util.List;

public interface DiscussionService {

    Discussion createDiscussion(Discussion discussion);
    List<Discussion> getDiscussions(Long groupId, Long bookId, Long challengeId);
    Discussion getDiscussion(Long discussionId);
    Discussion updateDiscussion(Long discussionId, String title, String content);
    void deleteDiscussion(Long discussionId);
}
