package com.app.users.model;

import java.time.LocalDateTime;

public class Follower {

    private Long followerId;
    private Long userId;
    private Long followingUserId;
    private LocalDateTime createdAt;

    public Follower() {}

    public Long getFollowerId() { return followerId; }
    public void setFollowerId(Long followerId) { this.followerId = followerId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getFollowingUserId() { return followingUserId; }
    public void setFollowingUserId(Long followingUserId) { this.followingUserId = followingUserId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
