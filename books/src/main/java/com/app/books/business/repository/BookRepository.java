package com.app.books.business.repository;

import com.app.books.business.repository.model.BookDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<BookDAO, Long> {
    List<BookDAO> findByGenre(String genre);
    List<BookDAO> findByAuthor(String author);
    List<BookDAO> findByTitleContainingIgnoreCase(String title);
    boolean existsById(Long bookId);
}
