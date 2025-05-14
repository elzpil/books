package com.app.books.business.repository;

import com.app.books.business.repository.model.ReadingProgressDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingProgressRepository extends JpaRepository<ReadingProgressDAO, Long> {
    List<ReadingProgressDAO> findByUserId(Long userId);
    Optional<ReadingProgressDAO> findByUserIdAndBookId(Long userId, Long bookId);
}
