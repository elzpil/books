package com.app.community.business.repository;

import com.app.community.business.repository.model.ChallengeDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeRepository extends JpaRepository<ChallengeDAO, Long> {
}
