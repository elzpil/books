package com.app.users.business.repository;

import com.app.users.business.repository.model.FollowerDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowerRepository extends JpaRepository<FollowerDAO, Long> {

    List<FollowerDAO> findByUserId(Long userId);
    List<FollowerDAO> findByFollowingUserId(Long followingUserId);
    Optional<FollowerDAO> findByUserIdAndFollowingUserId(Long userId, Long followingUserId);
}
