package com.app.users.business.service.impl;

import com.app.users.business.mapper.FollowerMapper;
import com.app.users.business.repository.FollowerRepository;
import com.app.users.business.repository.model.FollowerDAO;
import com.app.users.business.service.FollowerService;
import com.app.users.auth.util.JwtTokenUtil;
import com.app.users.exception.ResourceNotFoundException;
import com.app.users.model.Follower;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FollowerServiceImpl implements FollowerService {

    private final FollowerRepository followerRepository;
    private final FollowerMapper followerMapper;
    private final JwtTokenUtil jwtTokenUtil;

    public FollowerServiceImpl(FollowerRepository followerRepository, FollowerMapper followerMapper,
                               JwtTokenUtil jwtTokenUtil) {
        this.followerRepository = followerRepository;
        this.followerMapper = followerMapper;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public Follower followUser(String token, Long followUserId) {
        Long userId = jwtTokenUtil.extractUserId(token);

        if (userId.equals(followUserId)) {
            throw new IllegalArgumentException("You cannot follow yourself");
        }

        log.info("User {} is following user {}", userId, followUserId);

        FollowerDAO followerDAO = new FollowerDAO();
        followerDAO.setUserId(userId);
        followerDAO.setFollowingUserId(followUserId);
        followerDAO.setCreatedAt(LocalDateTime.now());

        followerRepository.save(followerDAO);
        return followerMapper.followerDAOtoFollower(followerDAO);
    }

    @Override
    public void unfollowUser(String token, Long followUserId) {
        Long userId = jwtTokenUtil.extractUserId(token);

        log.info("User {} is unfollowing user {}", userId, followUserId);

        FollowerDAO followerDAO = followerRepository.findByUserIdAndFollowingUserId(userId, followUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Follower", userId));

        followerRepository.delete(followerDAO);
    }


    @Override
    public List<Follower> getFollowers(Long userId) {

        List<FollowerDAO> followers = followerRepository.findByFollowingUserId(userId);

        return followers.stream()
                .map(followerMapper::followerDAOtoFollower)
                .collect(Collectors.toList());
    }

    @Override
    public List<Follower> getFollowing(Long userId) {

        List<FollowerDAO> following = followerRepository.findByUserId(userId);

        return following.stream()
                .map(followerMapper::followerDAOtoFollower)
                .collect(Collectors.toList());
    }
}
