package com.app.books.business.service.impl;

import com.app.books.auth.util.JwtTokenUtil;
import com.app.books.business.mapper.ReadingProgressMapper;
import com.app.books.business.repository.ReadingProgressRepository;
import com.app.books.business.repository.model.ReadingProgressDAO;
import com.app.books.business.service.ReadingProgressService;
import com.app.books.dto.ReadingProgressUpdateDTO;
import com.app.books.exception.UnauthorizedException;
import com.app.books.model.ReadingProgress;
import com.app.books.business.service.impl.UserServiceClient;  // Import UserServiceClient
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReadingProgressServiceImpl implements ReadingProgressService {

    private final ReadingProgressRepository repository;
    private final ReadingProgressMapper mapper;
    private final JwtTokenUtil jwtTokenUtil;

    public ReadingProgressServiceImpl(ReadingProgressRepository repository,
                                      ReadingProgressMapper mapper,
                                      JwtTokenUtil jwtTokenUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public ReadingProgress createProgress(ReadingProgress progress, String token) {
        Long userId = jwtTokenUtil.extractUserId(token.replace("Bearer ", ""));
        Optional<ReadingProgressDAO> existingProgress = repository.findByUserIdAndBookId(userId, progress.getBookId());

        if (existingProgress.isPresent()) {
            throw new IllegalStateException("Duplicate progress");
        }

        progress.setUserId(userId);
        progress.setLastUpdated(LocalDateTime.now());
        log.info("Creating reading progress: {}", progress);
        ReadingProgressDAO progressDAO = repository.save(mapper.progressToDAO(progress));
        return mapper.daoToProgress(progressDAO);
    }

    @Transactional
    @Override
    public ReadingProgress updateProgress(Long progressId, ReadingProgressUpdateDTO readingProgressUpdateDTO, String token) {
        int percentageRead = readingProgressUpdateDTO.getPercentageRead();
        log.info("Updating reading progress ID {} to {}%", progressId, percentageRead);

        ReadingProgressDAO existingProgress = repository.findById(progressId)
                .orElseThrow(() -> new RuntimeException("Reading progress not found"));

        if (!isAuthorized(token, existingProgress.getUserId())) {
            log.warn("Unauthorized attempt to update progress ID {} by user {}", progressId, jwtTokenUtil.extractUserId(token.replace("Bearer ", "")));
            throw new RuntimeException("Unauthorized to update this progress");
        }

        if (readingProgressUpdateDTO.getPercentageRead() != null) {
            existingProgress.setPercentageRead(percentageRead);
        }
        existingProgress.setPercentageRead(percentageRead);
        existingProgress.setLastUpdated(LocalDateTime.now());

        ReadingProgressDAO savedProgress = repository.save(existingProgress);

        log.info("Successfully updated progress ID {} to {}%", progressId, percentageRead);

        // Return the updated progress as a DTO
        return mapper.daoToProgress(savedProgress);
    }



    private boolean isAuthorized(String token, Long userId) {
        String cleanToken = token.replace("Bearer ", "");
        Long tokenUserId = jwtTokenUtil.extractUserId(cleanToken);
        System.out.println("usr id form token " + tokenUserId);
        String role = jwtTokenUtil.extractRole(cleanToken);
        return tokenUserId.equals(userId) || "ADMIN".equals(role);
    }

    @Override
    public List<ReadingProgress> getProgressByUser(Long userId) {
        log.info("Fetching reading progress for user ID: {}", userId);
        return repository.findByUserId(userId).stream()
                .map(mapper::daoToProgress)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ReadingProgress> getProgressById(Long progressId) {
        log.info("Fetching reading progress by ID: {}", progressId);
        return repository.findById(progressId).map(mapper::daoToProgress);
    }

    @Override
    public void deleteProgress(Long progressId, String token) {
        log.info("Attempting to delete reading progress with ID: {}", progressId);

        ReadingProgressDAO progress = repository.findById(progressId)
                .orElseThrow(() -> new RuntimeException("Reading progress not found"));

        if (!isAuthorized(token, progress.getUserId())) {
            log.warn("Unauthorized attempt to delete progress ID {} by user {}", progressId, jwtTokenUtil.extractUserId(token.replace("Bearer ", "")));
            throw new RuntimeException("Unauthorized to delete this progress");
        }

        repository.deleteById(progressId);
        log.info("Successfully deleted reading progress with ID {}", progressId);
    }
}
