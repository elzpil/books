package com.app.books.controller;

import com.app.books.business.service.ReadingProgressService;
import com.app.books.dto.ReadingProgressUpdateDTO;
import com.app.books.model.ReadingProgress;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/progress")
public class ReadingProgressController {

    private final ReadingProgressService readingProgressService;

    public ReadingProgressController(ReadingProgressService readingProgressService) {
        this.readingProgressService = readingProgressService;
    }

    @PostMapping
    public ResponseEntity<ReadingProgress> createProgress(@Valid @RequestBody ReadingProgress progress,
                                                          @RequestHeader("Authorization") String token) {
        log.info("Creating reading progress: {}", progress);
        ReadingProgress createdProgress = readingProgressService.createProgress(progress, token);
        return ResponseEntity.ok(createdProgress);
    }

    @PutMapping("/{progressId}")
    public ResponseEntity<ReadingProgress> updateProgress(
            @PathVariable Long progressId,
            @Valid @RequestBody ReadingProgressUpdateDTO readingProgressUpdateDTO,
            @RequestHeader("Authorization") String token) {

        log.info("Updating progress with ID {} to {}%", progressId, readingProgressUpdateDTO.getPercentageRead());

        ReadingProgress updated = readingProgressService.updateProgress(progressId, readingProgressUpdateDTO, token);

        return ResponseEntity.ok(updated);
    }


    @GetMapping
    public ResponseEntity<List<ReadingProgress>> getProgressByUser(@RequestParam Long userId) {
        log.info("Fetching reading progress for user ID: {}", userId);
        List<ReadingProgress> progressList = readingProgressService.getProgressByUser(userId);
        return ResponseEntity.ok(progressList);
    }

    @GetMapping("/{progressId}")
    public ResponseEntity<ReadingProgress> getProgressById(@PathVariable Long progressId) {
        log.info("Fetching reading progress by ID: {}", progressId);
        Optional<ReadingProgress> progress = readingProgressService.getProgressById(progressId);
        return progress.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{progressId}")
    public ResponseEntity<Void> deleteProgress(@PathVariable Long progressId,
                                               @RequestHeader("Authorization") String token) {
        log.info("Deleting reading progress with ID: {}", progressId);
        readingProgressService.deleteProgress(progressId, token);
        return ResponseEntity.noContent().build();
    }
}
