package com.app.books.business.service;

import com.app.books.dto.BookshelfUpdateDTO;
import com.app.books.model.BookshelfEntry;

import java.util.List;
import java.util.Optional;

public interface BookshelfService {
    BookshelfEntry addToBookshelf(BookshelfEntry entry, String token);
    List<BookshelfEntry> getUserBookshelf(Long userId);
    List<BookshelfEntry> getBookshelfByUserIdAndStatus(Long userId, String status);
    Optional<BookshelfEntry> getBookshelfEntry(Long bookshelfId);
    BookshelfEntry updateReadingStatus(Long bookshelfId, BookshelfUpdateDTO bookshelfUpdateDTO, String token);
    void removeFromBookshelf(Long bookshelfId, String token);
}
