package com.app.books.business.repository;

import com.app.books.business.repository.model.BookshelfDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookshelfRepository extends JpaRepository<BookshelfDAO, Long> {
    List<BookshelfDAO> findByUserId(Long userId);
    List<BookshelfDAO> findByUserIdAndStatus(Long userId, String status);

    Optional<BookshelfDAO> findByUserIdAndBookId(Long userId, Long bookId);
}
