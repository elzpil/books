package com.app.books.business.service;

import com.app.books.model.ReadingProgress;

import java.util.List;
import java.util.Optional;

public interface ReadingProgressService {
    ReadingProgress createProgress(ReadingProgress progress);
    ReadingProgress updateProgress(Long progressId, ReadingProgress progress, Long userId);
    List<ReadingProgress> getProgressByUser(Long userId);
    Optional<ReadingProgress> getProgressById(Long progressId);
    void deleteProgress(Long progressId);
}
