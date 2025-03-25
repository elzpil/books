package com.app.books.mapper;

import com.app.books.business.mapper.BookMapper;
import com.app.books.business.repository.model.BookDAO;
import com.app.books.model.Book;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BookMapperTest {

    // Initialize the mapper
    private final BookMapper bookMapper = Mappers.getMapper(BookMapper.class);

    @Test
    void testBookMapper() {
        // Create a BookDAO object and set its fields
        BookDAO bookDAO = new BookDAO();
        bookDAO.setTitle("Test Book");
        bookDAO.setAuthor("Test Author");
        bookDAO.setPublishedDate(LocalDate.now());
        bookDAO.setGenre("Fiction");

        // Map the BookDAO to Book using the bookMapper
        Book book = bookMapper.bookDAOToBook(bookDAO);

        // Verify that the mapping works correctly
        assertNotNull(book);
        assertEquals("Test Book", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertEquals(bookDAO.getPublishedDate(), book.getPublishedDate());
        assertEquals("Fiction", book.getGenre());
    }
}
