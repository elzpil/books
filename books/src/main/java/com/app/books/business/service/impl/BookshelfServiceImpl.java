package com.app.books.business.service.impl;

import com.app.books.auth.util.JwtTokenUtil;
import com.app.books.business.mapper.BookshelfMapper;
import com.app.books.business.repository.BookshelfRepository;
import com.app.books.business.repository.model.BookshelfDAO;
import com.app.books.business.service.BookshelfService;
import com.app.books.dto.BookshelfUpdateDTO;
import com.app.books.exception.ResourceNotFoundException;
import com.app.books.exception.UnauthorizedException;
import com.app.books.model.BookshelfEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookshelfServiceImpl implements BookshelfService {

    private final BookshelfRepository bookshelfRepository;
    private final BookshelfMapper bookshelfMapper;
    private final UserServiceClient userServiceClient;
    private final JwtTokenUtil jwtTokenUtil;

    public BookshelfServiceImpl(BookshelfRepository bookshelfRepository, BookshelfMapper bookshelfMapper,
                                UserServiceClient userServiceClient, JwtTokenUtil jwtTokenUtil) {
        this.bookshelfRepository = bookshelfRepository;
        this.bookshelfMapper = bookshelfMapper;
        this.userServiceClient = userServiceClient;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public BookshelfEntry addToBookshelf(BookshelfEntry entry) {
        log.info("Saving book {} to user {}'s bookshelf", entry.getBookId(), entry.getUserId());

        if (!userServiceClient.doesUserExist(entry.getUserId())) {
            throw new IllegalArgumentException("User with ID " + entry.getUserId() + " does not exist");
        }

        Optional<BookshelfDAO> existingEntry = bookshelfRepository.findByUserIdAndBookId(entry.getUserId(), entry.getBookId());
        if (existingEntry.isPresent()) {
            throw new IllegalStateException("This book is already in the user's bookshelf");
        }

        BookshelfDAO savedEntry = bookshelfRepository.save(bookshelfMapper.bookshelfEntryToBookshelfDAO(entry));
        BookshelfEntry result = bookshelfMapper.bookshelfDAOToBookshelfEntry(savedEntry);
        log.info("Saved bookshelf entry: {}", result);
        return result;
    }

    @Override
    public List<BookshelfEntry> getUserBookshelf(Long userId) {
        log.info("Fetching bookshelf for user {}", userId);
        return bookshelfRepository.findByUserId(userId).stream()
                .map(bookshelfMapper::bookshelfDAOToBookshelfEntry)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookshelfEntry> getBookshelfByUserIdAndStatus(Long userId, String status) {
        log.info("Fetching bookshelf for user {} with status '{}'", userId, status);
        return bookshelfRepository.findByUserIdAndStatus(userId, status).stream()
                .map(bookshelfMapper::bookshelfDAOToBookshelfEntry)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BookshelfEntry> getBookshelfEntry(Long bookshelfId) {
        return bookshelfRepository.findById(bookshelfId)
                .map(bookshelfMapper::bookshelfDAOToBookshelfEntry);
    }

    @Transactional
    @Override
    public BookshelfEntry updateReadingStatus(Long bookshelfId, BookshelfUpdateDTO bookshelfUpdateDTO, String token) {
        log.info("Updating reading status for bookshelf entry {}", bookshelfId);

        BookshelfDAO entry = bookshelfRepository.findById(bookshelfId)
                .orElseThrow(() -> new ResourceNotFoundException("Bookshelf entry", bookshelfId));

        if (!isAuthorized(token, entry.getUserId())) {
            log.warn("Unauthorized attempt to modify bookshelf entry {} by user", bookshelfId);
            throw new UnauthorizedException("User is not authorized to modify this bookshelf entry");
        }
        if (bookshelfUpdateDTO.getStatus() != null) {
            entry.setStatus(bookshelfUpdateDTO.getStatus());
        }

        BookshelfEntry updatedEntry = bookshelfMapper.bookshelfDAOToBookshelfEntry(bookshelfRepository.save(entry));

        log.info("Successfully updated bookshelf entry: {}", updatedEntry);
        return updatedEntry;
    }


    @Override
    public void removeFromBookshelf(Long bookshelfId, String token) {
        log.info("Attempting to remove bookshelf entry {} with token: {}", bookshelfId, token);
        BookshelfDAO entry = bookshelfRepository.findById(bookshelfId)
                .orElseThrow(() -> new ResourceNotFoundException("Bookshelf entry", bookshelfId));
        if (!isAuthorized(token, entry.getUserId())) {
            throw new UnauthorizedException("User is not authorized to modify this bookshelf entry");
        }

        bookshelfRepository.deleteById(bookshelfId);
        log.info("Bookshelf entry {} removed successfully", bookshelfId);
    }


    private boolean isAuthorized(String token, Long userId) {
        String cleanToken = token.replace("Bearer ", "");
        Long tokenUserId = jwtTokenUtil.extractUserId(cleanToken);
        String role = jwtTokenUtil.extractRole(cleanToken);
        return tokenUserId.equals(userId) || "ADMIN".equals(role);
    }

}
