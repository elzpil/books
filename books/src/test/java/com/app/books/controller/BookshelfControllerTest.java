package com.app.books.controller;

import com.app.books.business.service.BookshelfService;
import com.app.books.dto.BookshelfUpdateDTO;
import com.app.books.model.BookshelfEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
class BookshelfControllerTest {

    @Mock
    private BookshelfService bookshelfService;

    @InjectMocks
    private BookshelfController bookshelfController;

    private BookshelfEntry bookshelfEntry;
    private final Long bookshelfId = 1L;
    private final Long userId = 1L;
    private final String token = "Bearer mockedToken";

    @BeforeEach
    void setUp() {
        bookshelfEntry = new BookshelfEntry();
        bookshelfEntry.setId(bookshelfId);
        bookshelfEntry.setUserId(userId);
        bookshelfEntry.setBookId(1L);
        bookshelfEntry.setStatus("Reading");
    }

    @Test
    void addToBookshelf_ShouldReturnAddedEntry() {
        when(bookshelfService.addToBookshelf(bookshelfEntry, token)).thenReturn(bookshelfEntry);

        ResponseEntity<BookshelfEntry> response = bookshelfController.addToBookshelf(bookshelfEntry, token);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(bookshelfEntry.getId(), response.getBody().getId());

        verify(bookshelfService, times(1)).addToBookshelf(bookshelfEntry, token);
    }

    @Test
    void getUserBookshelf_ShouldReturnListOfEntries() {
        when(bookshelfService.getUserBookshelf(userId)).thenReturn(List.of(bookshelfEntry));

        ResponseEntity<List<BookshelfEntry>> response = bookshelfController.getUserBookshelf(userId);

        assertEquals(OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());

        verify(bookshelfService, times(1)).getUserBookshelf(userId);
    }

    @Test
    void getBookshelfEntry_EntryExists_ShouldReturnEntry() {
        when(bookshelfService.getBookshelfEntry(bookshelfId)).thenReturn(Optional.of(bookshelfEntry));

        ResponseEntity<BookshelfEntry> response = bookshelfController.getBookshelfEntry(bookshelfId);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(bookshelfEntry.getId(), response.getBody().getId());

        verify(bookshelfService, times(1)).getBookshelfEntry(bookshelfId);
    }

    @Test
    void getBookshelfEntry_EntryNotFound_ShouldReturnNotFound() {
        when(bookshelfService.getBookshelfEntry(bookshelfId)).thenReturn(Optional.empty());

        ResponseEntity<BookshelfEntry> response = bookshelfController.getBookshelfEntry(bookshelfId);

        assertEquals(NOT_FOUND, response.getStatusCode());
        verify(bookshelfService, times(1)).getBookshelfEntry(bookshelfId);
    }

    @Test
    void updateReadingStatus_ShouldReturnUpdatedEntry() {
        BookshelfUpdateDTO updateDTO = new BookshelfUpdateDTO();
        updateDTO.setStatus("Completed");

        when(bookshelfService.updateReadingStatus(bookshelfId, updateDTO, token)).thenReturn(bookshelfEntry);

        ResponseEntity<BookshelfEntry> response = bookshelfController.updateReadingStatus(bookshelfId, updateDTO, token);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(bookshelfEntry.getId(), response.getBody().getId());

        verify(bookshelfService, times(1)).updateReadingStatus(bookshelfId, updateDTO, token);
    }

    @Test
    void updateReadingStatus_EntryNotFound_ShouldReturnNotFound() {
        BookshelfUpdateDTO updateDTO = new BookshelfUpdateDTO();
        updateDTO.setStatus("Completed");

        when(bookshelfService.updateReadingStatus(bookshelfId, updateDTO, token)).thenReturn(null);

        ResponseEntity<BookshelfEntry> response = bookshelfController.updateReadingStatus(bookshelfId, updateDTO, token);

        assertEquals(NOT_FOUND, response.getStatusCode());
        verify(bookshelfService, times(1)).updateReadingStatus(bookshelfId, updateDTO, token);
    }

    @Test
    void removeFromBookshelf_ShouldReturnNoContent() {
        doNothing().when(bookshelfService).removeFromBookshelf(bookshelfId, token);

        ResponseEntity<Void> response = bookshelfController.removeFromBookshelf(bookshelfId, token);

        assertEquals(NO_CONTENT, response.getStatusCode());
        verify(bookshelfService, times(1)).removeFromBookshelf(bookshelfId, token);
    }
}
