package com.app.books.controller;

import com.app.books.auth.util.JwtTokenUtil;
import com.app.books.business.service.BookService;
import com.app.books.dto.BookUpdateDTO;
import com.app.books.exception.ResourceNotFoundException;
import com.app.books.model.Book;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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


    @PostMapping
    public ResponseEntity<Book> addBook(@Valid @RequestBody Book book,
                                        @RequestHeader("Authorization") String token) {
        log.info("Adding a new book");
        Book savedBook = bookService.saveBook(book, token);
        return ResponseEntity.ok(savedBook);
    }


    @GetMapping
    public ResponseEntity<List<Book>> getBooks(@RequestParam(required = false) String genre,
                                               @RequestParam(required = false) String author,
                                               @RequestParam(required = false) String title) {
        log.info("Fetching books with filters - Genre: {}, Author: {}, Title: {}", genre, author, title);
        List<Book> books = bookService.getBooks(genre, author, title);
        return ResponseEntity.ok(books);
    }
    @GetMapping("/all")
    public ResponseEntity<List<Book>> getAllBooks(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching all books (admin view), page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        List<Book> books = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<Book> getBookById(@PathVariable Long bookId) {
        log.info("Fetching book by ID: {}", bookId);
        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", bookId));
        return ResponseEntity.ok(book);
    }


    @PutMapping("/{bookId}")
    public ResponseEntity<Book> updateBook(@PathVariable Long bookId, @Valid @RequestBody BookUpdateDTO bookUpdateDTO,
                                           @RequestHeader("Authorization") String token) {
        log.info("Updating book with ID: {}", bookId);
        Book updatedBook = bookService.updateBook(bookId, bookUpdateDTO, token);
        return ResponseEntity.ok(updatedBook);
    }


    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId,
                                           @RequestHeader("Authorization") String token) {
        log.info("Deleting book with ID: {}", bookId);
        bookService.deleteBook(bookId, token);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/{bookId}")
    public ResponseEntity<Boolean> checkBookExists(@PathVariable Long bookId) {
        log.info("Checking if book with ID {} exists", bookId);
        boolean exists = bookService.bookExists(bookId);
        return ResponseEntity.ok(exists);
    }

    @PutMapping("/{bookId}/verify")
    public ResponseEntity<Void> verifyBook(@PathVariable Long bookId,
                                           @RequestHeader("Authorization") String token) {
        log.info("Verifying book with ID: {}", bookId);
        bookService.verify(bookId, token);
        return ResponseEntity.noContent().build();
    }

}

