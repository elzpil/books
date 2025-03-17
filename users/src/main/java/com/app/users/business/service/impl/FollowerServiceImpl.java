package com.app.users.business.service.impl;

import com.app.users.business.mapper.FollowerMapper;
import com.app.users.business.repository.FollowerRepository;
import com.app.users.business.repository.model.FollowerDAO;
import com.app.users.business.service.FollowerService;
import com.app.users.exception.ResourceNotFoundException;
import com.app.users.model.Follower;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowerServiceImpl implements FollowerService {

    private final FollowerRepository followerRepository;
    private final FollowerMapper followerMapper;

    public FollowerServiceImpl(FollowerRepository followerRepository, FollowerMapper followerMapper) {
        this.followerRepository = followerRepository;
        this.followerMapper = followerMapper;
    }

    @Override
    public Follower followUser(Long userId, Long followUserId) {
        if (userId.equals(followUserId)) {
            throw new IllegalArgumentException("You cannot follow yourself");
        }

        FollowerDAO followerDAO = new FollowerDAO();
        followerDAO.setUserId(userId);
        followerDAO.setFollowingUserId(followUserId);
        followerDAO.setCreatedAt(LocalDateTime.now());

        followerRepository.save(followerDAO);

        return followerMapper.followerDAOtoFollower(followerDAO);
    }

    @Override
    public void unfollowUser(Long userId, Long followUserId) {

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
