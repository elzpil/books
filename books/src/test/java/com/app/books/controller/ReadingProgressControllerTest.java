package com.app.books.controller;

import com.app.books.business.service.ReadingProgressService;
import com.app.books.dto.ReadingProgressUpdateDTO;
import com.app.books.model.ReadingProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
class ReadingProgressControllerTest {

    @Mock
    private ReadingProgressService readingProgressService;

    @InjectMocks
    private ReadingProgressController readingProgressController;

    private ReadingProgress progress;
    private final Long progressId = 1L;
    private final String token = "Bearer mockedToken";
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        progress = new ReadingProgress();
        progress.setId(progressId);
        progress.setUserId(userId);
        progress.setBookId(1L);
        progress.setPercentageRead(50);
    }

    @Test
    void createProgress_ShouldReturnCreatedProgress() {
        when(readingProgressService.createProgress(progress, token)).thenReturn(progress);

        ResponseEntity<ReadingProgress> response = readingProgressController.createProgress(progress, token);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(progress.getId(), response.getBody().getId());

        verify(readingProgressService, times(1)).createProgress(progress, token);
    }

    @Test
    void updateProgress_ShouldReturnUpdatedProgress() {
        ReadingProgressUpdateDTO updateDTO = new ReadingProgressUpdateDTO();
        updateDTO.setPercentageRead(80);

        when(readingProgressService.updateProgress(progressId, updateDTO, token)).thenReturn(progress);

        ResponseEntity<ReadingProgress> response = readingProgressController.updateProgress(progressId, updateDTO, token);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(progress.getId(), response.getBody().getId());

        verify(readingProgressService, times(1)).updateProgress(progressId, updateDTO, token);
    }

    @Test
    void getProgressByUser_ShouldReturnProgressList() {
        when(readingProgressService.getProgressByUser(userId)).thenReturn(List.of(progress));

        ResponseEntity<List<ReadingProgress>> response = readingProgressController.getProgressByUser(userId);

        assertEquals(OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());

        verify(readingProgressService, times(1)).getProgressByUser(userId);
    }

    @Test
    void getProgressById_ProgressExists_ShouldReturnProgress() {
        when(readingProgressService.getProgressById(progressId)).thenReturn(Optional.of(progress));

        ResponseEntity<ReadingProgress> response = readingProgressController.getProgressById(progressId);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(progress.getId(), response.getBody().getId());

        verify(readingProgressService, times(1)).getProgressById(progressId);
    }

    @Test
    void getProgressById_ProgressNotFound_ShouldReturnNotFound() {
        when(readingProgressService.getProgressById(progressId)).thenReturn(Optional.empty());

        ResponseEntity<ReadingProgress> response = readingProgressController.getProgressById(progressId);

        assertEquals(NOT_FOUND, response.getStatusCode());
        verify(readingProgressService, times(1)).getProgressById(progressId);
    }

    @Test
    void deleteProgress_ShouldReturnNoContent() {
        doNothing().when(readingProgressService).deleteProgress(progressId, token);

        ResponseEntity<Void> response = readingProgressController.deleteProgress(progressId, token);

        assertEquals(NO_CONTENT, response.getStatusCode());
        verify(readingProgressService, times(1)).deleteProgress(progressId, token);
    }
}
