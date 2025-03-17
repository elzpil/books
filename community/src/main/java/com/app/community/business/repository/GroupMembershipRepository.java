package com.app.community.business.repository;

import com.app.community.business.repository.model.GroupMembershipDAO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMembershipRepository extends JpaRepository<GroupMembershipDAO, Long> {
    List<GroupMembershipDAO> findByGroupId(Long groupId);
    void deleteByGroupIdAndUserId(Long groupId, Long userId);
}
