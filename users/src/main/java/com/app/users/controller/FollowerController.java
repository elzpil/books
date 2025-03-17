package com.app.users.controller;


import com.app.users.business.service.FollowerService;
import com.app.users.model.Follower;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class FollowerController {

    private final FollowerService followerService;

    public FollowerController(FollowerService followerService) {
        this.followerService = followerService;
    }

    @PostMapping("/{userId}/follow")
    public ResponseEntity<Follower> followUser(@PathVariable Long userId, @RequestParam Long followUserId) {
        Follower follower = followerService.followUser(userId, followUserId);
        return ResponseEntity.ok(follower);
    }

    @DeleteMapping("/{userId}/unfollow")
    public ResponseEntity<Void> unfollowUser(@PathVariable Long userId, @RequestParam Long followUserId) {
        followerService.unfollowUser(userId, followUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<Follower>> getFollowers(@PathVariable Long userId) {
        List<Follower> followers = followerService.getFollowers(userId);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<Follower>> getFollowing(@PathVariable Long userId) {
        List<Follower> following = followerService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }
}
