package com.app.books.controller;

import com.app.books.auth.util.JwtTokenUtil;
import com.app.books.business.service.BookService;
import com.app.books.dto.BookUpdateDTO;
import com.app.books.exception.ResourceNotFoundException;
import com.app.books.model.Book;
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
class BookControllerTest {

    @Mock
    private BookService bookService;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private BookController bookController;

    private Book book;
    private final Long bookId = 1L;
    private final String token = "Bearer mockedToken";

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setGenre("Fiction");
    }

    @Test
    void addBook_ShouldReturnAddedBook() {
        when(bookService.saveBook(book, token)).thenReturn(book);

        ResponseEntity<Book> response = bookController.addBook(book, token);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(book.getId(), response.getBody().getId());

        verify(bookService, times(1)).saveBook(book, token);
    }

    @Test
    void getBooks_ShouldReturnListOfBooks() {
        when(bookService.getBooks(null, null, null)).thenReturn(List.of(book));

        ResponseEntity<List<Book>> response = bookController.getBooks(null, null, null);

        assertEquals(OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());

        verify(bookService, times(1)).getBooks(null, null, null);
    }

    @Test
    void getBookById_BookExists_ShouldReturnBook() {
        when(bookService.getBookById(bookId)).thenReturn(Optional.of(book));

        ResponseEntity<Book> response = bookController.getBookById(bookId);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(book.getId(), response.getBody().getId());

        verify(bookService, times(1)).getBookById(bookId);
    }

    @Test
    void getBookById_BookNotFound_ShouldReturnNotFound() {

        when(bookService.getBookById(1L)).thenReturn(Optional.empty());

        try {
            ResponseEntity<Book> response = bookController.getBookById(1L);
            fail("Expected ResourceNotFoundException to be thrown");
        } catch (ResourceNotFoundException e) {
            assertEquals("Book not found with ID: 1", e.getMessage());
        }

        verify(bookService, times(1)).getBookById(1L);
    }


    @Test
    void updateBook_ShouldReturnUpdatedBook() {
        BookUpdateDTO bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.setTitle("Updated Title");
        bookUpdateDTO.setAuthor("Updated Author");
        bookUpdateDTO.setGenre("Updated Genre");

        when(bookService.updateBook(bookId, bookUpdateDTO, token)).thenReturn(book);

        ResponseEntity<Book> response = bookController.updateBook(bookId, bookUpdateDTO, token);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(book.getId(), response.getBody().getId());

        verify(bookService, times(1)).updateBook(bookId, bookUpdateDTO, token);
    }

    @Test
    void deleteBook_ShouldReturnNoContent() {
        doNothing().when(bookService).deleteBook(bookId, token);

        ResponseEntity<Void> response = bookController.deleteBook(bookId, token);

        assertEquals(NO_CONTENT, response.getStatusCode());
        verify(bookService, times(1)).deleteBook(bookId, token);
    }

    @Test
    void deleteBook_BookNotFound_ShouldReturnNotFound() {

        doThrow(new ResourceNotFoundException("Book", bookId)).when(bookService).deleteBook(bookId, token);

        try {
            ResponseEntity<Void> response = bookController.deleteBook(bookId, token);
            fail("Expected ResourceNotFoundException to be thrown");
        } catch (ResourceNotFoundException e) {
            assertEquals("Book not found with ID: " + bookId, e.getMessage());
        }

        verify(bookService, times(1)).deleteBook(bookId, token);
    }


    @Test
    void checkBookExists_ShouldReturnTrue_WhenBookExists() {
        when(bookService.bookExists(bookId)).thenReturn(true);

        ResponseEntity<Boolean> response = bookController.checkBookExists(bookId);

        assertEquals(OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(bookService, times(1)).bookExists(bookId);
    }

    @Test
    void checkBookExists_ShouldReturnFalse_WhenBookDoesNotExist() {
        when(bookService.bookExists(bookId)).thenReturn(false);

        ResponseEntity<Boolean> response = bookController.checkBookExists(bookId);

        assertEquals(OK, response.getStatusCode());
        assertFalse(response.getBody());
        verify(bookService, times(1)).bookExists(bookId);
    }

    @Test
    void verifyBook_ShouldReturnNoContent_WhenBookIsVerified() {
        doNothing().when(bookService).verify(bookId, token);

        ResponseEntity<Void> response = bookController.verifyBook(bookId, token);

        assertEquals(NO_CONTENT, response.getStatusCode());
        verify(bookService, times(1)).verify(bookId, token);
    }

    @Test
    void verifyBook_ShouldThrowException_WhenBookNotFound() {

        doThrow(new ResourceNotFoundException("Book", bookId)).when(bookService).verify(bookId, token);

        try {
            bookController.verifyBook(bookId, token);
            fail("Expected ResourceNotFoundException to be thrown");
        } catch (ResourceNotFoundException e) {
            assertEquals("Book not found with ID: " + bookId, e.getMessage());
        }

        verify(bookService, times(1)).verify(bookId, token);
    }
}
