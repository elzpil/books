package com.app.books.controller;

import com.app.books.business.service.BookService;
import com.app.books.exception.ResourceNotFoundException;
import com.app.books.model.Book;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<Book> addBook(@Valid @RequestBody Book book) {
        log.info("Adding a new book");
        Book savedBook = bookService.saveBook(book);
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

    @GetMapping("/{bookId}")
    public ResponseEntity<Book> getBookById(@PathVariable Long bookId) {
        log.info("Fetching book by ID: {}", bookId);
        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", bookId));
        return ResponseEntity.ok(book);
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<Book> updateBook(@PathVariable Long bookId, @Valid @RequestBody Book book) {
        log.info("Updating book with ID: {}", bookId);
        Book updatedBook = bookService.updateBook(bookId, book);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        log.info("Deleting book with ID: {}", bookId);
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/{bookId}")
    public ResponseEntity<Void> checkUserExists(@PathVariable Long bookId) {
        boolean bookExists = bookService.bookExists(bookId);
        if (bookExists) {
            return ResponseEntity.noContent().build(); // HTTP 204 No Content if user exists
        } else {
            return ResponseEntity.notFound().build(); // HTTP 404 Not Found if user does not exist
        }
    }
}
