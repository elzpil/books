package com.app.community.business.service;

import com.app.community.business.repository.model.DiscussionDAO;
import com.app.community.dto.DiscussionUpdateDTO;
import com.app.community.model.Discussion;

import java.util.List;

public interface DiscussionService {

    Discussion createDiscussion(Discussion discussion, String token);
    List<Discussion> getDiscussions(Long groupId, Long bookId, Long challengeId);
    Discussion getDiscussion(Long discussionId);
    Discussion updateDiscussion(Long discussionId, DiscussionUpdateDTO discussionUpdateDTO, String token);
    void deleteDiscussion(Long discussionId, String token);
}
