package com.app.books.business.service.impl;

import com.app.books.auth.util.JwtTokenUtil;
import com.app.books.business.mapper.BookMapper;
import com.app.books.business.repository.BookRepository;
import com.app.books.business.repository.model.BookDAO;
import com.app.books.dto.BookUpdateDTO;
import com.app.books.exception.ResourceNotFoundException;
import com.app.books.exception.UnauthorizedException;
import com.app.books.model.Book;
import org.apache.catalina.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    @Mock
    private EmailServiceImpl emailService;

    @Mock
    private UserServiceClient userServiceClient;

    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookService = new BookServiceImpl(bookRepository, bookMapper, jwtTokenUtil, emailService, userServiceClient);
    }

    @Test
    void saveBook_ShouldReturnSavedBook() {
        // Arrange
        String token = "Bearer adminToken";
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
        String token = "Bearer adminToken";
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

        Long bookId = 1L;
        String token = "Bearer userToken";
        BookUpdateDTO bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.setTitle("Updated Book");

        when(jwtTokenUtil.extractRole(token.replace("Bearer ", ""))).thenReturn("USER");

        assertThrows(ResourceNotFoundException.class, () -> bookService.updateBook(bookId, bookUpdateDTO, token));
    }

    @Test
    void updateBook_ShouldThrowResourceNotFoundException_WhenBookNotFound() {

        Long bookId = 999L;
        BookUpdateDTO bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.setTitle("Updated Book");
        String token = "Bearer adminToken";

        when(jwtTokenUtil.extractRole(token.replace("Bearer ", ""))).thenReturn("ADMIN");
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.updateBook(bookId, bookUpdateDTO, token));
    }

    @Test
    void deleteBook_ShouldDeleteBook() {
        // Arrange
        Long bookId = 1L;
        String token = "Bearer adminToken";

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
        String token = "Bearer userToken";

        when(jwtTokenUtil.extractRole(token.replace("Bearer ", ""))).thenReturn("USER");

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> bookService.deleteBook(bookId, token));
    }

    @Test
    void deleteBook_ShouldThrowResourceNotFoundException_WhenBookNotFound() {
        // Arrange
        Long bookId = 999L; // Book does not exist
        String token = "Bearer adminToken";
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

    @Test
    void verify_ShouldVerifyBook_WhenAdmin() {
        // Arrange
        Long bookId = 1L;
        String token = "Bearer adminToken";

        BookDAO bookDAO = new BookDAO();
        bookDAO.setVerified(Boolean.FALSE);
        bookDAO.setTitle("Test Book");

        when(jwtTokenUtil.extractRole(token.replace("Bearer ", ""))).thenReturn("ADMIN");
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookDAO));
        when(bookRepository.save(any(BookDAO.class))).thenReturn(bookDAO);

        // Act
        bookService.verify(bookId, token);

        // Assert
        assertTrue(bookDAO.getVerified());  // Check that the book's verification status was set to true
        verify(bookRepository, times(1)).save(bookDAO);
    }

    @Test
    void verify_ShouldThrowUnauthorizedException_WhenNotAdmin() {
        // Arrange
        Long bookId = 1L;
        String token = "Bearer userToken";

        when(jwtTokenUtil.extractRole(token.replace("Bearer ", ""))).thenReturn("USER");

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> bookService.verify(bookId, token));
    }

    @Test
    void verify_ShouldThrowResourceNotFoundException_WhenBookNotFound() {
        // Arrange
        Long bookId = 999L;
        String token = "Bearer adminToken";

        when(jwtTokenUtil.extractRole(token.replace("Bearer ", ""))).thenReturn("ADMIN");
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bookService.verify(bookId, token));
    }

    @Test
    void getBookById_ShouldReturnBook_WhenBookExists() {
        // Arrange
        Long bookId = 1L;
        BookDAO bookDAO = new BookDAO();
        bookDAO.setTitle("Test Book");
        bookDAO.setAuthor("Test Author");

        Book expectedBook = new Book();
        expectedBook.setTitle("Test Book");
        expectedBook.setAuthor("Test Author");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookDAO));
        when(bookMapper.bookDAOToBook(bookDAO)).thenReturn(expectedBook);

        // Act
        Optional<Book> book = bookService.getBookById(bookId);

        // Assert
        assertTrue(book.isPresent());
        assertEquals("Test Book", book.get().getTitle());
    }


    @Test
    void getBookById_ShouldReturnEmpty_WhenBookDoesNotExist() {
        // Arrange
        Long bookId = 999L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // Act
        Optional<Book> book = bookService.getBookById(bookId);

        // Assert
        assertFalse(book.isPresent());
    }

    @Test
    void bookExists_ShouldReturnTrue_WhenBookExists() {
        // Arrange
        Long bookId = 1L;

        when(bookRepository.existsById(bookId)).thenReturn(true);

        // Act
        boolean exists = bookService.bookExists(bookId);

        // Assert
        assertTrue(exists);
    }

    @Test
    void bookExists_ShouldReturnFalse_WhenBookDoesNotExist() {
        // Arrange
        Long bookId = 999L;

        when(bookRepository.existsById(bookId)).thenReturn(false);

        // Act
        boolean exists = bookService.bookExists(bookId);

        // Assert
        assertFalse(exists);
    }

}
