package com.app.community.business.service.impl;

import com.app.community.auth.util.JwtTokenUtil;
import com.app.community.business.mapper.DiscussionMapper;
import com.app.community.business.repository.DiscussionRepository;
import com.app.community.business.repository.model.DiscussionDAO;
import com.app.community.business.service.DiscussionService;
import com.app.community.dto.DiscussionUpdateDTO;
import com.app.community.exception.ResourceNotFoundException;
import com.app.community.exception.UnauthorizedException;
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
    private final JwtTokenUtil jwtTokenUtil;

    public DiscussionServiceImpl(DiscussionRepository discussionRepository,
                                 DiscussionMapper discussionMapper,
                                 UserServiceClient userServiceClient,
                                 BookServiceClient bookServiceClient,
                                 JwtTokenUtil jwtTokenUtil) {
        this.discussionRepository = discussionRepository;
        this.discussionMapper = discussionMapper;
        this.userServiceClient = userServiceClient;
        this.bookServiceClient = bookServiceClient;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public Discussion createDiscussion(Discussion discussion, String token) {
        Long userId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));
        log.info("Validating user with ID: {}", userId);

        if (discussion.getBookId() != null) {
            log.info("Validating book with ID: {}", discussion.getBookId());
            if (!bookServiceClient.doesBookExist(discussion.getBookId(), token)) {
                throw new IllegalArgumentException("Book with ID " + discussion.getBookId() + " does not exist.");
            }
        }
        discussion.setCreatedAt(LocalDateTime.now());
        discussion.setUserId(userId);
        DiscussionDAO discussionDAO = discussionRepository.save(discussionMapper.discussionToDiscussionDAO(discussion));

        log.info("Saving new discussion: {}", discussionDAO);
        return discussionMapper.discussionDAOToDiscussion(discussionDAO);
    }

    @Override
    public List<Discussion> getDiscussions(String token, Long groupId, Long bookId, Long challengeId) {
        String cleanToken = token.replace("Bearer ", "");
        String userRole = jwtTokenUtil.extractRole(cleanToken);

        // If all parameters are null, only allow access for admins
        if (groupId == null && bookId == null && challengeId == null) {
            if (!"ADMIN".equals(userRole)) {
                log.warn("Unauthorized attempt to get all discussions by non-admin user");
                throw new UnauthorizedException("Only admins can retrieve all discussions.");
            }
        }
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
        log.info("Getting discussion: {}", discussionId);
        return discussionDAO.map(discussionMapper::discussionDAOToDiscussion).orElse(null);
    }

    @Transactional
    @Override
    public Discussion updateDiscussion(Long discussionId, DiscussionUpdateDTO discussionUpdateDTO, String token) {

        Optional<DiscussionDAO> existingDiscussion = discussionRepository.findById(discussionId);
        if (existingDiscussion.isPresent()) {
            Long userId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));
            if (!isAuthorized(token, existingDiscussion.get().getUserId())) {
                log.warn("Unauthorized attempt to delete discussion ID {} by user ID {}", discussionId, userId);
                throw new UnauthorizedException("You are not authorized to delete this discussion");
            }
            DiscussionDAO discussionDAO = existingDiscussion.get();
            if( discussionUpdateDTO.getTitle() != null) {
                discussionDAO.setTitle(discussionUpdateDTO.getTitle());
            }
            if( discussionUpdateDTO.getContent() != null) {
                discussionDAO.setContent(discussionUpdateDTO.getContent());
            }
            return discussionMapper.discussionDAOToDiscussion(discussionRepository.save(discussionDAO));
        }
        return null;
    }

    @Override
    public void deleteDiscussion(Long discussionId, String token) {
        log.info("Attempting to delete discussion ID {}", discussionId);

        DiscussionDAO existingDiscussion= discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion", discussionId));

        // Check if the user is authorized
        if (!isAuthorized(token, existingDiscussion.getUserId())) {
            log.warn("Unauthorized attempt to delete discussion ID {} by user ID {}", discussionId, existingDiscussion.getUserId());
            throw new UnauthorizedException("You are not authorized to delete this discussion");
        }

        discussionRepository.deleteById(discussionId);
        log.info("Successfully deleted discussion ID {}", discussionId);
    }

    private boolean isAuthorized(String token, Long userId) {
        Long tokenUserId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));
        String role = jwtTokenUtil.extractRole(token.replace("Bearer ", ""));
        return tokenUserId.equals(userId) || "ADMIN".equals(role);
    }
}
