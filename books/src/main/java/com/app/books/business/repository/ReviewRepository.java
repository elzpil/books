package com.app.books.business.repository;

import com.app.books.business.repository.model.ReviewDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewDAO, Long> {
    List<ReviewDAO> findByBookId(Long bookId);

    Optional<ReviewDAO> findByUserIdAndBookId(Long userId, Long bookId);

}
