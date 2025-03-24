package com.app.books.controller;

import com.app.books.auth.util.JwtTokenUtil;
import com.app.books.business.service.BookService;
import com.app.books.dto.BookUpdateDTO;
import com.app.books.exception.ResourceNotFoundException;
import com.app.books.model.Book;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@Slf4j
@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Book> addBook(@Valid @RequestBody Book book) {
        log.info("Adding a new book");
        Book savedBook = bookService.saveBook(book);
        return ResponseEntity.ok(savedBook);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Book>> getBooks(@RequestParam(required = false) String genre,
                                               @RequestParam(required = false) String author,
                                               @RequestParam(required = false) String title) {
        log.info("Fetching books with filters - Genre: {}, Author: {}, Title: {}", genre, author, title);
        List<Book> books = bookService.getBooks(genre, author, title);
        return ResponseEntity.ok(books);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{bookId}")
    public ResponseEntity<Book> getBookById(@PathVariable Long bookId) {
        log.info("Fetching book by ID: {}", bookId);
        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", bookId));
        return ResponseEntity.ok(book);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{bookId}")
    public ResponseEntity<Book> updateBook(@PathVariable Long bookId, @Valid @RequestBody BookUpdateDTO bookUpdateDTO) {
        log.info("Updating book with ID: {}", bookId);
        Book updatedBook = bookService.updateBook(bookId, bookUpdateDTO);
        return ResponseEntity.ok(updatedBook);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        log.info("Deleting book with ID: {}", bookId);
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }
}

