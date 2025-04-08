package com.app.books.business.repository;

import com.app.books.business.repository.model.BookDAO;
import com.app.books.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<BookDAO, Long> {
    List<BookDAO> findByGenre(String genre);
    List<BookDAO> findByAuthor(String author);
    List<BookDAO> findByTitleContainingIgnoreCase(String title);
    boolean existsById(Long bookId);
    @Query(value = "SELECT * FROM books b WHERE " +
            "(:genre IS NULL OR b.genre = :genre) OR " +
            "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) OR " +
            "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) ",
            nativeQuery = true)
    List<BookDAO> findBooksByFilters(@Param("genre") String genre,
                                     @Param("author") String author,
                                     @Param("title") String title);



}
