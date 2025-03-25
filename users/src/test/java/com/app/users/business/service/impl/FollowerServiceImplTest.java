package com.app.users.business.service.impl;

import com.app.users.auth.util.JwtTokenUtil;
import com.app.users.business.mapper.FollowerMapper;
import com.app.users.business.repository.FollowerRepository;
import com.app.users.business.repository.model.FollowerDAO;
import com.app.users.business.service.FollowerService;
import com.app.users.exception.ResourceNotFoundException;
import com.app.users.model.Follower;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowerServiceImplTest {

    @Mock
    private FollowerRepository followerRepository;

    @Mock
    private FollowerMapper followerMapper;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private FollowerServiceImpl followerService;

    private final String token = "mockedToken";
    private final Long userId = 1L;
    private final Long followUserId = 2L;
    private FollowerDAO followerDAO;
    private Follower follower;

    @BeforeEach
    void setUp() {
        followerDAO = new FollowerDAO();
        followerDAO.setUserId(userId);
        followerDAO.setFollowingUserId(followUserId);
        followerDAO.setCreatedAt(LocalDateTime.now());

        follower = new Follower();
        follower.setUserId(userId);
        follower.setFollowingUserId(followUserId);
    }

    @Test
    void followUser_ShouldSaveFollower() {
        when(jwtTokenUtil.extractUserId(token)).thenReturn(userId);
        when(followerMapper.followerDAOtoFollower(any(FollowerDAO.class))).thenReturn(follower);
        when(followerRepository.save(any(FollowerDAO.class))).thenReturn(followerDAO);

        Follower result = followerService.followUser(token, followUserId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(followUserId, result.getFollowingUserId());

        verify(followerRepository, times(1)).save(any(FollowerDAO.class));
    }

    @Test
    void followUser_ShouldThrowException_WhenFollowingSelf() {
        when(jwtTokenUtil.extractUserId(token)).thenReturn(userId);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            followerService.followUser(token, userId);
        });

        assertEquals("You cannot follow yourself", exception.getMessage());
    }

    @Test
    void unfollowUser_ShouldDeleteFollower() {
        when(jwtTokenUtil.extractUserId(token)).thenReturn(userId);
        when(followerRepository.findByUserIdAndFollowingUserId(userId, followUserId))
                .thenReturn(Optional.of(followerDAO));

        followerService.unfollowUser(token, followUserId);

        verify(followerRepository, times(1)).delete(followerDAO);
    }

    @Test
    void unfollowUser_ShouldThrowException_WhenFollowerNotFound() {
        when(jwtTokenUtil.extractUserId(token)).thenReturn(userId);
        when(followerRepository.findByUserIdAndFollowingUserId(userId, followUserId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            followerService.unfollowUser(token, followUserId);
        });
    }

    @Test
    void getFollowers_ShouldReturnListOfFollowers() {
        when(followerRepository.findByFollowingUserId(followUserId)).thenReturn(List.of(followerDAO));
        when(followerMapper.followerDAOtoFollower(any(FollowerDAO.class))).thenReturn(follower);

        List<Follower> result = followerService.getFollowers(followUserId);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());

        verify(followerRepository, times(1)).findByFollowingUserId(followUserId);
    }

    @Test
    void getFollowing_ShouldReturnListOfFollowing() {
        when(followerRepository.findByUserId(userId)).thenReturn(List.of(followerDAO));
        when(followerMapper.followerDAOtoFollower(any(FollowerDAO.class))).thenReturn(follower);

        List<Follower> result = followerService.getFollowing(userId);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(followUserId, result.get(0).getFollowingUserId());

        verify(followerRepository, times(1)).findByUserId(userId);
    }
}
