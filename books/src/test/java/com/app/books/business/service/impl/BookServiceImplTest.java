package com.app.books.business.service.impl;

import com.app.books.auth.util.JwtTokenUtil;
import com.app.books.business.mapper.BookMapper;
import com.app.books.business.repository.BookRepository;
import com.app.books.business.repository.model.BookDAO;
import com.app.books.dto.BookUpdateDTO;
import com.app.books.exception.ResourceNotFoundException;
import com.app.books.exception.UnauthorizedException;
import com.app.books.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookService = new BookServiceImpl(bookRepository, bookMapper, jwtTokenUtil);
    }

    @Test
    void saveBook_ShouldReturnSavedBook() {
        // Arrange
        String token = "Bearer adminToken";  // Mocked JWT token for admin user
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPublishedDate(LocalDate.now());
        book.setGenre("Fiction");

        BookDAO bookDAO = new BookDAO();
        bookDAO.setTitle("Test Book");
        bookDAO.setAuthor("Test Author");
        bookDAO.setPublishedDate(LocalDate.now());
        bookDAO.setGenre("Fiction");

        when(jwtTokenUtil.extractRole(token.replace("Bearer ", ""))).thenReturn("ADMIN");
        when(bookMapper.bookToBookDAO(any(Book.class))).thenReturn(bookDAO);
        when(bookRepository.save(any(BookDAO.class))).thenReturn(bookDAO);
        when(bookMapper.bookDAOToBook(any(BookDAO.class))).thenReturn(book);

        // Act
        Book savedBook = bookService.saveBook(book, token);

        // Assert
        assertNotNull(savedBook);
        assertEquals("Test Book", savedBook.getTitle());
        verify(bookRepository, times(1)).save(any(BookDAO.class));
    }

    @Test
    void saveBook_ShouldThrowUnauthorizedException_WhenNotAdmin() {
        // Arrange
        String token = "Bearer userToken";  // Mocked JWT token for non-admin user
        Book book = new Book();
        book.setTitle("Test Book");

        when(jwtTokenUtil.extractRole(token.replace("Bearer ", ""))).thenReturn("USER");

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> bookService.saveBook(book, token));
    }

    @Test
    void getAllBooks_ShouldReturnListOfBooks() {
        // Arrange
        Pageable pageable = mock(Pageable.class);
        BookDAO bookDAO = new BookDAO();
        bookDAO.setTitle("Test Book");
        bookDAO.setAuthor("Test Author");
        bookDAO.setPublishedDate(LocalDate.now());
        bookDAO.setGenre("Fiction");

        Page<BookDAO> bookPage = mock(Page.class);
        when(bookPage.getContent()).thenReturn(List.of(bookDAO));
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.bookDAOToBook(any(BookDAO.class))).thenReturn(new Book());

        // Act
        List<Book> books = bookService.getAllBooks(pageable);

        // Assert
        assertNotNull(books);
        assertFalse(books.isEmpty());
        verify(bookRepository, times(1)).findAll(pageable);
    }

    @Test
    void updateBook_ShouldReturnUpdatedBook() {
        // Arrange
        Long bookId = 1L;
        String token = "Bearer adminToken";  // Mocked JWT token for admin user
        BookUpdateDTO bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.setTitle("Updated Book");

        BookDAO existingBook = new BookDAO();
        existingBook.setTitle("Old Book");

        when(jwtTokenUtil.extractRole(token.replace("Bearer ", ""))).thenReturn("ADMIN");
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(BookDAO.class))).thenReturn(existingBook);
        when(bookMapper.bookDAOToBook(any(BookDAO.class))).thenReturn(new Book());

        // Act
        Book updatedBook = bookService.updateBook(bookId, bookUpdateDTO, token);

        // Assert
        assertNotNull(updatedBook);
        verify(bookRepository, times(1)).save(existingBook);
    }

    @Test
    void updateBook_ShouldThrowUnauthorizedException_WhenNotAdmin() {
        // Arrange
        Long bookId = 1L;
        String token = "Bearer userToken";  // Mocked JWT token for non-admin user
        BookUpdateDTO bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.setTitle("Updated Book");

        when(jwtTokenUtil.extractRole(token.replace("Bearer ", ""))).thenReturn("USER");

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> bookService.updateBook(bookId, bookUpdateDTO, token));
    }

    @Test
    void updateBook_ShouldThrowResourceNotFoundException_WhenBookNotFound() {
        // Arrange
        Long bookId = 999L; // Book does not exist
        BookUpdateDTO bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.setTitle("Updated Book");
        String token = "Bearer adminToken";  // Mocked JWT token for admin user

        when(jwtTokenUtil.extractRole(token.replace("Bearer ", ""))).thenReturn("ADMIN");
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bookService.updateBook(bookId, bookUpdateDTO, token));
    }

    @Test
    void deleteBook_ShouldDeleteBook() {
        // Arrange
        Long bookId = 1L;
        String token = "Bearer adminToken";  // Mocked JWT token for admin user

        when(jwtTokenUtil.extractRole(token.replace("Bearer ", ""))).thenReturn("ADMIN");
        when(bookRepository.existsById(bookId)).thenReturn(true);

        // Act
        bookService.deleteBook(bookId, token);

        // Assert
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    void deleteBook_ShouldThrowUnauthorizedException_WhenNotAdmin() {
        // Arrange
        Long bookId = 1L;
        String token = "Bearer userToken";  // Mocked JWT token for non-admin user

        when(jwtTokenUtil.extractRole(token.replace("Bearer ", ""))).thenReturn("USER");

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> bookService.deleteBook(bookId, token));
    }

    @Test
    void deleteBook_ShouldThrowResourceNotFoundException_WhenBookNotFound() {
        // Arrange
        Long bookId = 999L; // Book does not exist
        String token = "Bearer adminToken";  // Mocked JWT token for admin user
        when(jwtTokenUtil.extractRole(token.replace("Bearer ", ""))).thenReturn("ADMIN");
        when(bookRepository.existsById(bookId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bookService.deleteBook(bookId, token));
    }

    @Test
    void getBooks_ShouldReturnBooksByFilters() {
        // Arrange
        String genre = "Fiction";
        String author = "Test Author";
        String title = "Test Book";

        BookDAO bookDAO = new BookDAO();
        bookDAO.setTitle("Test Book");
        bookDAO.setAuthor("Test Author");
        bookDAO.setPublishedDate(LocalDate.now());
        bookDAO.setGenre("Fiction");

        when(bookRepository.findBooksByFilters(genre, author, title))
                .thenReturn(List.of(bookDAO));
        when(bookMapper.bookDAOToBook(any(BookDAO.class))).thenReturn(new Book());

        // Act
        List<Book> books = bookService.getBooks(genre, author, title);

        // Assert
        assertNotNull(books);
        assertFalse(books.isEmpty());
        verify(bookRepository, times(1)).findBooksByFilters(genre, author, title);
    }
}
