package com.app.books.business.service.impl;

import com.app.books.auth.util.JwtTokenUtil;
import com.app.books.business.mapper.ReadingProgressMapper;
import com.app.books.business.repository.ReadingProgressRepository;
import com.app.books.business.repository.model.ReadingProgressDAO;
import com.app.books.dto.ReadingProgressUpdateDTO;
import com.app.books.exception.UnauthorizedException;
import com.app.books.model.ReadingProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReadingProgressServiceImplTest {

    @Mock
    private ReadingProgressRepository repository;

    @Mock
    private ReadingProgressMapper mapper;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    private ReadingProgressServiceImpl readingProgressService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        readingProgressService = new ReadingProgressServiceImpl(repository, mapper, jwtTokenUtil);

    }

    @Test
    void createProgress_ShouldReturnCreatedProgress() {
        // Arrange
        String token = "Bearer validToken";
        ReadingProgress progress = new ReadingProgress();
        progress.setBookId(1L);
        progress.setPercentageRead(50);

        ReadingProgressDAO progressDAO = new ReadingProgressDAO();
        progressDAO.setBookId(1L);
        progressDAO.setPercentageRead(50);

        when(jwtTokenUtil.extractUserId(token.replace("Bearer ", ""))).thenReturn(1L);
        when(repository.findByUserIdAndBookId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(repository.save(any(ReadingProgressDAO.class))).thenReturn(progressDAO);
        when(mapper.progressToDAO(any(ReadingProgress.class))).thenReturn(progressDAO);
        when(mapper.daoToProgress(any(ReadingProgressDAO.class))).thenReturn(progress);

        // Act
        ReadingProgress createdProgress = readingProgressService.createProgress(progress, token);

        // Assert
        assertNotNull(createdProgress);
        assertEquals(50, createdProgress.getPercentageRead());
        verify(repository, times(1)).save(any(ReadingProgressDAO.class));
    }

    @Test
    void createProgress_ShouldThrowIllegalStateException_WhenDuplicateProgress() {
        // Arrange
        String token = "Bearer validToken";
        ReadingProgress progress = new ReadingProgress();
        progress.setBookId(1L);
        progress.setPercentageRead(50);

        ReadingProgressDAO existingProgressDAO = new ReadingProgressDAO();
        existingProgressDAO.setBookId(1L);
        existingProgressDAO.setPercentageRead(50);

        when(jwtTokenUtil.extractUserId(token.replace("Bearer ", ""))).thenReturn(1L);
        when(repository.findByUserIdAndBookId(anyLong(), anyLong())).thenReturn(Optional.of(existingProgressDAO));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> readingProgressService.createProgress(progress, token));
    }

    @Test
    void updateProgress_ShouldReturnUpdatedProgress() {
        // Arrange
        Long progressId = 1L;
        String token = "Bearer validToken";
        ReadingProgressUpdateDTO updateDTO = new ReadingProgressUpdateDTO();
        updateDTO.setPercentageRead(75);

        ReadingProgressDAO existingProgressDAO = new ReadingProgressDAO();
        existingProgressDAO.setId(progressId);
        existingProgressDAO.setPercentageRead(50);
        existingProgressDAO.setUserId(1L);

        ReadingProgressDAO updatedProgressDAO = new ReadingProgressDAO();
        updatedProgressDAO.setId(progressId);
        updatedProgressDAO.setPercentageRead(75);  // Updated value
        updatedProgressDAO.setUserId(1L);

        when(jwtTokenUtil.extractUserId(token.replace("Bearer ", ""))).thenReturn(1L);
        when(repository.findById(progressId)).thenReturn(Optional.of(existingProgressDAO));
        when(repository.save(any(ReadingProgressDAO.class))).thenReturn(updatedProgressDAO);

        ReadingProgress updatedProgress = new ReadingProgress();
        updatedProgress.setId(progressId);
        updatedProgress.setPercentageRead(75);
        updatedProgress.setUserId(1L);

        when(mapper.daoToProgress(any(ReadingProgressDAO.class))).thenReturn(updatedProgress);

        // Act
        ReadingProgress result = readingProgressService.updateProgress(progressId, updateDTO, token);

        // Assert
        assertNotNull(result);
        assertEquals(75, result.getPercentageRead());
        verify(repository, times(1)).save(any(ReadingProgressDAO.class));
    }


    @Test
    void updateProgress_ShouldThrowUnauthorizedException_WhenUserIsNotAuthorized() {
        // Arrange
        Long progressId = 1L;
        String token = "Bearer invalidToken";
        ReadingProgressUpdateDTO updateDTO = new ReadingProgressUpdateDTO();
        updateDTO.setPercentageRead(75);

        ReadingProgressDAO existingProgressDAO = new ReadingProgressDAO();
        existingProgressDAO.setId(progressId);
        existingProgressDAO.setUserId(2L);  // Different user ID from token

        when(jwtTokenUtil.extractUserId(token.replace("Bearer ", ""))).thenReturn(1L);
        when(repository.findById(progressId)).thenReturn(Optional.of(existingProgressDAO));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> readingProgressService.updateProgress(progressId, updateDTO, token));
    }

    @Test
    void getProgressById_ShouldReturnReadingProgress() {
        // Arrange
        Long progressId = 1L;
        ReadingProgressDAO progressDAO = new ReadingProgressDAO();
        progressDAO.setId(progressId);
        progressDAO.setPercentageRead(50);
        progressDAO.setUserId(1L);

        ReadingProgress readingProgress = new ReadingProgress();
        readingProgress.setId(progressId);
        readingProgress.setPercentageRead(50);
        readingProgress.setUserId(1L);

        when(repository.findById(progressId)).thenReturn(Optional.of(progressDAO));
        when(mapper.daoToProgress(any(ReadingProgressDAO.class))).thenReturn(readingProgress);

        // Act
        Optional<ReadingProgress> progress = readingProgressService.getProgressById(progressId);

        // Assert
        assertTrue(progress.isPresent());
        assertEquals(50, progress.get().getPercentageRead());
    }

    @Test
    void deleteProgress_ShouldDeleteProgress() {
        // Arrange
        Long progressId = 1L;
        String token = "Bearer validToken";
        ReadingProgressDAO existingProgressDAO = new ReadingProgressDAO();
        existingProgressDAO.setId(progressId);
        existingProgressDAO.setUserId(1L);

        when(repository.findById(progressId)).thenReturn(Optional.of(existingProgressDAO));
        when(jwtTokenUtil.extractUserId(token.replace("Bearer ", ""))).thenReturn(1L);

        // Act
        readingProgressService.deleteProgress(progressId, token);

        // Assert
        verify(repository, times(1)).deleteById(progressId);
    }

    @Test
    void deleteProgress_ShouldThrowUnauthorizedException_WhenUserIsNotAuthorized() {
        // Arrange
        Long progressId = 1L;
        String token = "Bearer invalidToken";
        ReadingProgressDAO existingProgressDAO = new ReadingProgressDAO();
        existingProgressDAO.setId(progressId);
        existingProgressDAO.setUserId(2L);  // Different user ID from token

        when(repository.findById(progressId)).thenReturn(Optional.of(existingProgressDAO));
        when(jwtTokenUtil.extractUserId(token.replace("Bearer ", ""))).thenReturn(1L);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> readingProgressService.deleteProgress(progressId, token));
    }
}
