package com.app.books.business.service.impl;

import com.app.books.auth.util.JwtTokenUtil;
import com.app.books.business.mapper.BookshelfMapper;
import com.app.books.business.repository.BookshelfRepository;
import com.app.books.dto.BookshelfUpdateDTO;
import com.app.books.exception.ResourceNotFoundException;
import com.app.books.exception.UnauthorizedException;
import com.app.books.model.BookshelfEntry;
import com.app.books.business.repository.model.BookshelfDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookshelfServiceImplTest {

    @Mock
    private BookshelfRepository bookshelfRepository;

    @Mock
    private BookshelfMapper bookshelfMapper;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private BookshelfServiceImpl bookshelfService;

    private Long userId;
    private String token;
    private BookshelfEntry bookshelfEntry;
    private BookshelfDAO bookshelfDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize mock objects using setters
        userId = 1L;
        token = "Bearer validToken";
        bookshelfEntry = new BookshelfEntry();
        bookshelfEntry.setBookId(1L);
        bookshelfEntry.setUserId(userId);
        bookshelfEntry.setStatus("Reading");
        bookshelfEntry.setCreatedAt(LocalDate.now());

        bookshelfDAO = new BookshelfDAO();
        bookshelfDAO.setId(1L);
        bookshelfDAO.setUserId(userId);
        bookshelfDAO.setBookId(1L);
        bookshelfDAO.setStatus("Reading");
        bookshelfDAO.setCreatedAt(LocalDateTime.now());

        // Set up the bookshelf service with constructor
        bookshelfService = new BookshelfServiceImpl(bookshelfRepository, bookshelfMapper, jwtTokenUtil);
    }



    @Test
    void addToBookshelf_AlreadyExist_ShouldThrowIllegalStateException() {
        // Arrange
        when(jwtTokenUtil.extractUserId(token.replace("Bearer ", ""))).thenReturn(userId);
        when(bookshelfRepository.findByUserIdAndBookId(userId, bookshelfEntry.getBookId())).thenReturn(Optional.of(bookshelfDAO));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookshelfService.addToBookshelf(bookshelfEntry, token);
        });
        assertEquals("This book is already in the user's bookshelf", exception.getMessage());
    }

    @Test
    void getUserBookshelf_Success() {
        // Arrange
        when(bookshelfRepository.findByUserId(userId)).thenReturn(List.of(bookshelfDAO));
        when(bookshelfMapper.bookshelfDAOToBookshelfEntry(bookshelfDAO)).thenReturn(bookshelfEntry);

        // Act
        List<BookshelfEntry> result = bookshelfService.getUserBookshelf(userId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(userId, result.get(0).getUserId());
    }

    @Test
    void updateReadingStatus_ResourceNotFoundException() {
        // Arrange
        BookshelfUpdateDTO bookshelfUpdateDTO = new BookshelfUpdateDTO();
        bookshelfDAO.setStatus("Read");
        when(bookshelfRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            bookshelfService.updateReadingStatus(1L, bookshelfUpdateDTO, token);
        });
        assertEquals("Bookshelf entry not found with ID: 1", exception.getMessage());
    }

    @Test
    void updateReadingStatus_UnauthorizedException() {
        // Arrange
        BookshelfUpdateDTO bookshelfUpdateDTO = new BookshelfUpdateDTO();
        bookshelfDAO.setStatus("Read");
        when(bookshelfRepository.findById(1L)).thenReturn(Optional.of(bookshelfDAO));
        when(jwtTokenUtil.extractUserId(token.replace("Bearer ", ""))).thenReturn(2L); // Unauthorized user

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            bookshelfService.updateReadingStatus(1L, bookshelfUpdateDTO, token);
        });
        assertEquals("User is not authorized to modify this bookshelf entry", exception.getMessage());
    }

    @Test
    void removeFromBookshelf_Success() {
        // Arrange
        when(bookshelfRepository.findById(1L)).thenReturn(Optional.of(bookshelfDAO));
        when(jwtTokenUtil.extractUserId(token.replace("Bearer ", ""))).thenReturn(userId);

        // Act
        bookshelfService.removeFromBookshelf(1L, token);

        // Assert
        verify(bookshelfRepository, times(1)).deleteById(1L);
    }

    @Test
    void removeFromBookshelf_ResourceNotFoundException() {
        // Arrange
        when(bookshelfRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            bookshelfService.removeFromBookshelf(1L, token);
        });
        assertEquals("Bookshelf entry not found with ID: 1", exception.getMessage());
    }

    @Test
    void removeFromBookshelf_UnauthorizedException() {
        // Arrange
        when(bookshelfRepository.findById(1L)).thenReturn(Optional.of(bookshelfDAO));
        when(jwtTokenUtil.extractUserId(token.replace("Bearer ", ""))).thenReturn(2L); // Unauthorized user

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            bookshelfService.removeFromBookshelf(1L, token);
        });
        assertEquals("User is not authorized to modify this bookshelf entry", exception.getMessage());
    }
}
