package com.app.community.business.repository;

import com.app.community.business.repository.model.DiscussionDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussionRepository extends JpaRepository<DiscussionDAO, Long> {

    List<DiscussionDAO> findByGroupId(Long groupId);

    List<DiscussionDAO> findByBookId(Long bookId);

    List<DiscussionDAO> findByChallengeId(Long challengeId);
}
