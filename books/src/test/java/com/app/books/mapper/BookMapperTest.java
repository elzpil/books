package com.app.books.mapper;

import com.app.books.business.mapper.BookMapper;
import com.app.books.business.repository.model.BookDAO;
import com.app.books.model.Book;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BookMapperTest {

    private final BookMapper bookMapper = Mappers.getMapper(BookMapper.class);

    @Test
    void testBookMapper() {
        BookDAO bookDAO = new BookDAO();
        bookDAO.setTitle("Test Book");
        bookDAO.setAuthor("Test Author");
        bookDAO.setPublishedDate(LocalDate.now());
        bookDAO.setGenre("Fiction");

        Book book = bookMapper.bookDAOToBook(bookDAO);

        assertNotNull(book);
        assertEquals("Test Book", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertEquals(bookDAO.getPublishedDate(), book.getPublishedDate());
        assertEquals("Fiction", book.getGenre());
    }
}
