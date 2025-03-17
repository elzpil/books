package com.app.books.business.service.impl;

import com.app.books.business.mapper.BookMapper;
import com.app.books.business.repository.BookRepository;
import com.app.books.business.repository.model.BookDAO;
import com.app.books.business.service.BookService;
import com.app.books.exception.ResourceNotFoundException;
import com.app.books.model.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public Book saveBook(Book book) {
        log.info("Saving a new book: {}", book.getTitle());
        BookDAO bookDAO = bookRepository.save(bookMapper.bookToBookDAO(book));
        return bookMapper.bookDAOToBook(bookDAO);
    }

    @Override
    public List<Book> getAllBooks(Pageable pageable) {
        log.info("Fetching all books with pagination: {}", pageable);
        Page<BookDAO> bookPage = bookRepository.findAll(pageable);
        return bookPage.getContent()
                .stream()
                .map(bookMapper::bookDAOToBook)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Book> getBookById(Long bookId) {
        log.info("Fetching book by ID: {}", bookId);
        return bookRepository.findById(bookId)
                .map(bookMapper::bookDAOToBook);
    }

    @Transactional
    @Override
    public Book updateBook(Long bookId, Book book) {
        log.info("Updating book with ID: {}", bookId);

        BookDAO existingBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", bookId));

        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setDescription(book.getDescription());
        existingBook.setPublishedDate(book.getPublishedDate());
        existingBook.setGenre(book.getGenre());

        BookDAO updatedBook = bookRepository.save(existingBook);
        return bookMapper.bookDAOToBook(updatedBook);
    }

    @Override
    public void deleteBook(Long bookId) {
        log.info("Deleting book with ID: {}", bookId);

        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Book", bookId);
        }

        bookRepository.deleteById(bookId);
        log.info("Book with ID {} successfully deleted", bookId);
    }

    @Override
    public List<Book> getBooks(String genre, String author, String title) {
        log.info("Fetching books with filters - Genre: {}, Author: {}, Title: {}", genre, author, title);

        List<BookDAO> books;

        if (genre != null) {
            books = bookRepository.findByGenre(genre);
        } else if (author != null) {
            books = bookRepository.findByAuthor(author);
        } else if (title != null) {
            books = bookRepository.findByTitleContainingIgnoreCase(title);
        } else {
            books = bookRepository.findAll();
        }

        return books.stream()
                .map(bookMapper::bookDAOToBook)
                .collect(Collectors.toList());
    }
}
