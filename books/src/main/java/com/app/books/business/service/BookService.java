package com.app.books.business.service;

import com.app.books.dto.BookUpdateDTO;
import com.app.books.model.Book;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Book saveBook(Book book);
    List<Book> getAllBooks(Pageable pageable);
    Optional<Book> getBookById(Long bookId);
    Book updateBook(Long bookId, BookUpdateDTO bookUpdateDTO);
    void deleteBook(Long bookId);
    List<Book> getBooks(String genre, String author, String title);
    boolean bookExists(Long bookId);
}
