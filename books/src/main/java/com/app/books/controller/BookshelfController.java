package com.app.books.controller;

import com.app.books.business.service.BookshelfService;
import com.app.books.model.BookshelfEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/bookshelf")
public class BookshelfController {

    private final BookshelfService bookshelfService;

    public BookshelfController(BookshelfService bookshelfService) {
        this.bookshelfService = bookshelfService;
    }

    @PostMapping
    public ResponseEntity<BookshelfEntry> addToBookshelf(@RequestBody BookshelfEntry entry) {
        log.info("Adding book {} to user {}'s bookshelf", entry.getBookId(), entry.getUserId());
        BookshelfEntry savedEntry = bookshelfService.addToBookshelf(entry);
        log.info("Book successfully added to bookshelf: {}", savedEntry);
        return ResponseEntity.ok(savedEntry);
    }

    @GetMapping
    public ResponseEntity<List<BookshelfEntry>> getUserBookshelf(@RequestParam Long userId) {
        log.info("Fetching bookshelf for user {}", userId);
        List<BookshelfEntry> bookshelf = bookshelfService.getUserBookshelf(userId);
        log.info("Bookshelf for user {} contains {} books", userId, bookshelf.size());
        return ResponseEntity.ok(bookshelf);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<BookshelfEntry>> getBookshelfByUserIdAndStatus(
            @RequestParam Long userId, @RequestParam String status) {
        log.info("Fetching bookshelf for user {} with status '{}'", userId, status);
        List<BookshelfEntry> entries = bookshelfService.getBookshelfByUserIdAndStatus(userId, status);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/{bookshelfId}")
    public ResponseEntity<BookshelfEntry> getBookshelfEntry(@PathVariable Long bookshelfId) {
        log.info("Fetching bookshelf entry {}", bookshelfId);
        Optional<BookshelfEntry> entry = bookshelfService.getBookshelfEntry(bookshelfId);
        return entry.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Bookshelf entry {} not found", bookshelfId);
                    return ResponseEntity.notFound().build();
                });
    }

    @PutMapping("/{bookshelfId}")
    public ResponseEntity<BookshelfEntry> updateReadingStatus(
            @PathVariable Long bookshelfId, @RequestParam String status) {
        log.info("Updating reading status for bookshelf entry {} to '{}'", bookshelfId, status);
        BookshelfEntry updatedEntry = bookshelfService.updateReadingStatus(bookshelfId, status);
        if (updatedEntry == null) {
            log.warn("Bookshelf entry {} not found", bookshelfId);
            return ResponseEntity.notFound().build();
        }
        log.info("Updated bookshelf entry: {}", updatedEntry);
        return ResponseEntity.ok(updatedEntry);
    }

    @DeleteMapping("/{bookshelfId}")
    public ResponseEntity<Void> removeFromBookshelf(@PathVariable Long bookshelfId) {
        log.info("Removing bookshelf entry {}", bookshelfId);
        bookshelfService.removeFromBookshelf(bookshelfId);
        log.info("Bookshelf entry {} removed successfully", bookshelfId);
        return ResponseEntity.noContent().build();
    }
}
