package com.app.community.business.repository;

import com.app.community.business.repository.model.GroupDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<GroupDAO, Long> {
    @Query("SELECT g FROM GroupDAO g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<GroupDAO> searchGroupsByName(@Param("name") String name);

    @Query("SELECT g FROM GroupDAO g WHERE g.privacySetting = 'PUBLIC' AND " +
            "(LOWER(g.name) LIKE %:query% OR LOWER(g.description) LIKE %:query%)")
    List<GroupDAO> findByPrivacySettingAndQuery(@Param("query") String query);

}
