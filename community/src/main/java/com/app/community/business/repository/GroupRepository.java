package com.app.community.business.repository;

import com.app.community.business.repository.model.GroupDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupRepository extends JpaRepository<GroupDAO, Long> {
    @Query("SELECT g FROM GroupDAO g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<GroupDAO> searchGroupsByName(@Param("name") String name);
}
