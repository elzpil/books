package com.app.community.business.repository;

import com.app.community.business.repository.model.GroupDAO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<GroupDAO, Long> {
}
