package com.app.users.controller;

import com.app.users.business.service.FollowerService;
import com.app.users.model.Follower;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
class FollowerControllerTest {

    @Mock
    private FollowerService followerService;

    @InjectMocks
    private FollowerController followerController;

    private final String token = "Bearer mockedToken";
    private final Long userId = 1L;
    private final Long followUserId = 2L;
    private Follower follower;

    @BeforeEach
    void setUp() {
        follower = new Follower();
        follower.setUserId(userId);
        follower.setFollowingUserId(followUserId);
    }

    @Test
    void followUser_ShouldReturnFollower() {
        when(followerService.followUser(token, followUserId)).thenReturn(follower);

        ResponseEntity<Follower> response = followerController.followUser(token, followUserId);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userId, response.getBody().getUserId());
        assertEquals(followUserId, response.getBody().getFollowingUserId());

        verify(followerService, times(1)).followUser(token, followUserId);
    }

    @Test
    void unfollowUser_ShouldReturnNoContent() {
        doNothing().when(followerService).unfollowUser(token, followUserId);

        ResponseEntity<Void> response = followerController.unfollowUser(token, followUserId);

        assertEquals(NO_CONTENT, response.getStatusCode());
        verify(followerService, times(1)).unfollowUser(token, followUserId);
    }

    @Test
    void getFollowers_ShouldReturnListOfFollowers() {
        when(followerService.getFollowers(userId)).thenReturn(List.of(follower));

        ResponseEntity<List<Follower>> response = followerController.getFollowers(userId);

        assertEquals(OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        assertEquals(userId, response.getBody().get(0).getUserId());

        verify(followerService, times(1)).getFollowers(userId);
    }

    @Test
    void getFollowing_ShouldReturnListOfFollowing() {
        when(followerService.getFollowing(userId)).thenReturn(List.of(follower));

        ResponseEntity<List<Follower>> response = followerController.getFollowing(userId);

        assertEquals(OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        assertEquals(followUserId, response.getBody().get(0).getFollowingUserId());

        verify(followerService, times(1)).getFollowing(userId);
    }
}
