package com.app.users.business.service;

import com.app.users.model.Follower;

import java.util.List;

public interface FollowerService {

    Follower followUser(Long userId, Long followUserId);

    void unfollowUser(Long userId, Long followUserId);

    List<Follower> getFollowers(Long userId);

    List<Follower> getFollowing(Long userId);
}
