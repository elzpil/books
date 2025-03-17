package com.app.community.business.service.impl;

import com.app.community.business.mapper.DiscussionMapper;
import com.app.community.business.repository.DiscussionRepository;
import com.app.community.business.repository.model.CommentDAO;
import com.app.community.business.repository.model.DiscussionDAO;
import com.app.community.business.service.DiscussionService;
import com.app.community.model.Discussion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DiscussionServiceImpl implements DiscussionService {

    private final DiscussionRepository discussionRepository;
    private final DiscussionMapper discussionMapper;
    private final UserServiceClient userServiceClient;

    private final BookServiceClient bookServiceClient;

    public DiscussionServiceImpl(DiscussionRepository discussionRepository,
                                 DiscussionMapper discussionMapper,
                                 UserServiceClient userServiceClient,
                                 BookServiceClient bookServiceClient) {
        this.discussionRepository = discussionRepository;
        this.discussionMapper = discussionMapper;
        this.userServiceClient = userServiceClient;
        this.bookServiceClient = bookServiceClient;
    }

    @Override
    public Discussion createDiscussion(Discussion discussion) {
        Long userId = discussion.getUserId();
        log.info("Validating user with ID: {}", userId);
        if (!userServiceClient.doesUserExist(userId)) {
            throw new IllegalArgumentException("User with ID " + userId + " does not exist.");
        }
        if(discussion.getBookId() != null){
            log.info("Validating book with ID: {}", discussion.getBookId());
            if (!bookServiceClient.doesBookExist(discussion.getBookId())) {
                throw new IllegalArgumentException("Book with ID " + userId + " does not exist.");
            }
        }

        DiscussionDAO discussionDAO = discussionRepository.save(discussionMapper.discussionToDiscussionDAO(discussion));
        discussionDAO.setCreatedAt(LocalDateTime.now());
        log.info("Saving new discussion: {}", discussionDAO);
        return discussionMapper.discussionDAOToDiscussion(discussionDAO);
    }

    @Override
    public List<Discussion> getDiscussions(Long groupId, Long bookId, Long challengeId) {
        List<DiscussionDAO> discussionDAOs;
        log.info("Getting discussions");
        if (groupId != null) {
            discussionDAOs = discussionRepository.findByGroupId(groupId);
        } else if (bookId != null) {
            discussionDAOs = discussionRepository.findByBookId(bookId);
        } else if (challengeId != null) {
            discussionDAOs = discussionRepository.findByChallengeId(challengeId);
        } else {
            discussionDAOs = discussionRepository.findAll();
        }
        return discussionDAOs.stream()
                .map(discussionMapper::discussionDAOToDiscussion)
                .collect(Collectors.toList());
    }

    @Override
    public Discussion getDiscussion(Long discussionId) {
        Optional<DiscussionDAO> discussionDAO = discussionRepository.findById(discussionId);
        log.info("getting disxusssion: {}", discussionId);
        return discussionDAO.map(discussionMapper::discussionDAOToDiscussion).orElse(null);
    }

    @Transactional
    @Override
    public Discussion updateDiscussion(Long discussionId, String title, String content) {
        Optional<DiscussionDAO> existingDiscussion = discussionRepository.findById(discussionId);
        if (existingDiscussion.isPresent()) {
            DiscussionDAO discussionDAO = existingDiscussion.get();
            discussionDAO.setTitle(title);
            discussionDAO.setContent(content);
            return discussionMapper.discussionDAOToDiscussion(discussionRepository.save(discussionDAO));
        }
        return null;
    }

    @Override
    public void deleteDiscussion(Long discussionId) {
        log.info("deleting disxusssion with id: {}", discussionId);
        discussionRepository.deleteById(discussionId);
    }
}
