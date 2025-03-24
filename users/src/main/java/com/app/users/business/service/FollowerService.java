package com.app.users.business.service;

import com.app.users.model.Follower;

import java.util.List;

public interface FollowerService {

    Follower followUser(String token, Long followUserId);

    void unfollowUser(String token, Long followUserId);

    List<Follower> getFollowers(Long userId);

    List<Follower> getFollowing(Long userId);
}
