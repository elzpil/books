package com.app.books.business.service.impl;

import com.app.books.business.mapper.ReadingProgressMapper;
import com.app.books.business.repository.ReadingProgressRepository;
import com.app.books.business.repository.model.ReadingProgressDAO;
import com.app.books.business.service.ReadingProgressService;
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
    private final UserServiceClient userServiceClient;  // Add the UserServiceClient

    public ReadingProgressServiceImpl(ReadingProgressRepository repository, ReadingProgressMapper mapper, UserServiceClient userServiceClient) {
        this.repository = repository;
        this.mapper = mapper;
        this.userServiceClient = userServiceClient;
    }

    @Override
    public ReadingProgress createProgress(ReadingProgress progress) {

        if (!userServiceClient.doesUserExist(progress.getUserId())) {
            throw new IllegalArgumentException("User with ID " + progress.getUserId() + " does not exist");
        }

        Optional<ReadingProgressDAO> existingProgress = repository.findByUserIdAndBookId(progress.getUserId(), progress.getBookId());

        if (existingProgress.isPresent()) {
            throw new IllegalStateException("Duplicate progress");
        }

        log.info("Creating reading progress: {}", progress);
        ReadingProgressDAO progressDAO = repository.save(mapper.progressToDAO(progress));
        return mapper.daoToProgress(progressDAO);
    }

    @Transactional
    @Override
    public ReadingProgress updateProgress(Long progressId, ReadingProgress updatedProgress, Long userId) {
        // Check if the user exists using UserServiceClient
        if (!userServiceClient.doesUserExist(userId)) {
            throw new IllegalArgumentException("User with ID " + userId + " does not exist");
        }

        log.info("Updating progress ID {} for user {}: {}", progressId, userId, updatedProgress);
        Optional<ReadingProgressDAO> existingProgressOpt = repository.findById(progressId);

        if (existingProgressOpt.isPresent()) {
            ReadingProgressDAO existingProgress = existingProgressOpt.get();

            if (!existingProgress.getUserId().equals(userId)) {
                log.warn("Unauthorized attempt to update progress ID {} by user {}", progressId, userId);
                throw new RuntimeException("Unauthorized to update this progress");
            }

            existingProgress.setPercentageRead(updatedProgress.getPercentageRead());
            existingProgress.setLastUpdated(LocalDateTime.now());

            ReadingProgressDAO savedProgress = repository.save(existingProgress);
            log.info("Successfully updated progress ID {}", progressId);
            return mapper.daoToProgress(savedProgress);
        } else {
            log.error("Reading progress not found with ID: {}", progressId);
            throw new RuntimeException("Reading progress not found");
        }
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
    public void deleteProgress(Long progressId) {
        log.info("Deleting reading progress with ID: {}", progressId);
        repository.deleteById(progressId);
    }
}
