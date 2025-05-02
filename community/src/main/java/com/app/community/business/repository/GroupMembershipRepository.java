package com.app.community.business.repository;

import com.app.community.business.repository.model.GroupMembershipDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository

public interface GroupMembershipRepository extends JpaRepository<GroupMembershipDAO, Long> {
    List<GroupMembershipDAO> findByGroupId(Long groupId);
    Optional<GroupMembershipDAO> findByGroupIdAndUserId(Long groupId, Long userId);
    void deleteByGroupIdAndUserId(Long groupId, Long userId);

    List<GroupMembershipDAO> findByUserId(Long userId);
}
