package com.app.community.business.repository;

import com.app.community.business.repository.model.ChallengeDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<ChallengeDAO, Long> {


    @Query("SELECT c FROM ChallengeDAO c WHERE " +
            "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:description IS NULL OR LOWER(c.description) LIKE LOWER(CONCAT('%', :description, '%')))")
    List<ChallengeDAO> searchChallenges2(@Param("name") String name,
                                        @Param("description") String description);

    @Query(value = "SELECT * FROM challenges b WHERE " +
            "(LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%'))) OR " +
            "(LOWER(b.description) LIKE LOWER(CONCAT('%', :description, '%'))) ",
            nativeQuery = true)
    List<ChallengeDAO> searchChallenges(@Param("name") String name,
                                        @Param("description") String description);

    @Query("SELECT c FROM ChallengeDAO c LEFT JOIN ChallengeParticipantDAO cp " +
            "ON c.id = cp.challengeId " +
            "GROUP BY c.id " +
            "ORDER BY COUNT(cp.participantId) DESC")
    List<ChallengeDAO> findChallengesSortedByPopularity();

    List<ChallengeDAO> findAllById(Iterable<Long> ids);


}
