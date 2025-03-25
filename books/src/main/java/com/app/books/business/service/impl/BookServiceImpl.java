package com.app.books.business.service.impl;

import com.app.books.auth.util.JwtTokenUtil;
import com.app.books.business.mapper.BookMapper;
import com.app.books.business.repository.BookRepository;
import com.app.books.business.repository.model.BookDAO;
import com.app.books.business.service.BookService;
import com.app.books.dto.BookUpdateDTO;
import com.app.books.exception.ResourceNotFoundException;
import com.app.books.exception.UnauthorizedException;
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
    private final JwtTokenUtil jwtTokenUtil;

    public BookServiceImpl(BookRepository bookRepository, BookMapper bookMapper, JwtTokenUtil jwtTokenUtil) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public Book saveBook(Book book, String token) {
        if (!isAdmin(token)) {
            throw new UnauthorizedException("Only admins can create books");
        }
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
    public Book updateBook(Long bookId, BookUpdateDTO bookUpdateDTO, String token) {

        if (!isAdmin(token)) {
            throw new UnauthorizedException("Only admins can create books");
        }
        log.info("Updating book with ID: {}", bookId);

        BookDAO existingBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", bookId));

        // Log before updates
        log.info("Before updating: Title = {}, Author = {}, Genre = {}",
                existingBook.getTitle(), existingBook.getAuthor(), existingBook.getGenre());

        if (bookUpdateDTO.getTitle() != null) {
            existingBook.setTitle(bookUpdateDTO.getTitle());
        }
        if (bookUpdateDTO.getAuthor() != null) {
            existingBook.setAuthor(bookUpdateDTO.getAuthor());
        }
        if (bookUpdateDTO.getDescription() != null) {
            existingBook.setDescription(bookUpdateDTO.getDescription());
        }
        if (bookUpdateDTO.getPublishedDate() != null) {
            existingBook.setPublishedDate(bookUpdateDTO.getPublishedDate());
        }
        if (bookUpdateDTO.getGenre() != null) {
            existingBook.setGenre(bookUpdateDTO.getGenre());
        }

        // Log after updates
        log.info("After updating: Title = {}, Author = {}, Genre = {}",
                existingBook.getTitle(), existingBook.getAuthor(), existingBook.getGenre());

        BookDAO updatedBook = bookRepository.save(existingBook);
        log.info("Updated book details from repository: Title = {}, Author = {}, Genre = {}",
                updatedBook.getTitle(), updatedBook.getAuthor(), updatedBook.getGenre());

        Book book = bookMapper.bookDAOToBook(updatedBook);
        log.info("Mapped book: Title = {}, Author = {}, Genre = {}",
                book.getTitle(), book.getAuthor(), book.getGenre());

        return book;
    }



    @Override
    public void deleteBook(Long bookId, String token) {
        if (!isAdmin(token)) {
            throw new UnauthorizedException("Only admins can delete books");
        }
        log.info("Deleting book with ID: {}", bookId);

        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Book", bookId);
        }

        bookRepository.deleteById(bookId);
        log.info("Book with ID {} successfully deleted", bookId);
    }

    /*@Override
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
    }*/

    @Override
    public boolean bookExists(Long bookId) {
        return bookRepository.existsById(bookId);
    }

    @Override
    public List<Book> getBooks(String genre, String author, String title) {
        log.info("Fetching books with filters - Genre: {}, Author: {}, Title: {}", genre, author, title);

        // Use the custom query that supports all filters
        List<BookDAO> books = bookRepository.findBooksByFilters(genre, author, title);

        // Convert BookDAO to Book before returning
        return books.stream()
                .map(bookMapper::bookDAOToBook)
                .collect(Collectors.toList());
    }

    private boolean isAdmin(String token) {
        String cleanToken = token.replace("Bearer ", "");
        String role = jwtTokenUtil.extractRole(cleanToken);
        return  "ADMIN".equals(role);
    }
}
