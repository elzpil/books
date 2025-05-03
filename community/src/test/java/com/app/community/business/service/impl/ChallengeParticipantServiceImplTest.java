package com.app.community.business.service.impl;

import com.app.community.auth.util.JwtTokenUtil;
import com.app.community.business.mapper.ChallengeMapper;
import com.app.community.business.mapper.ChallengeParticipantMapper;
import com.app.community.business.repository.ChallengeParticipantRepository;
import com.app.community.business.repository.ChallengeRepository;
import com.app.community.business.repository.model.ChallengeParticipantDAO;
import com.app.community.dto.ChallengeParticipantUpdateDTO;
import com.app.community.exception.UnauthorizedException;
import com.app.community.model.ChallengeParticipant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ChallengeParticipantServiceImplTest {

    @Mock
    private ChallengeParticipantRepository participantRepository;

    @Mock
    private ChallengeRepository challengeRepository;
    @Mock
    private ChallengeParticipantMapper participantMapper;

    @Mock
    private ChallengeMapper challengeMapper;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    private ChallengeParticipantServiceImpl participantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        participantService = new ChallengeParticipantServiceImpl(participantRepository,
                participantMapper, challengeRepository, challengeMapper, jwtTokenUtil);
    }

    @Test
    void joinChallenge_ShouldAddParticipant_WhenUserIsNotAlreadyJoined() {
        // Arrange
        Long challengeId = 1L;
        String token = "Bearer validToken";
        Long userId = 2L;

        ChallengeParticipantDAO participantDAO = new ChallengeParticipantDAO();
        participantDAO.setUserId(userId);
        participantDAO.setChallengeId(challengeId);
        participantDAO.setProgress(0);
        participantDAO.setCompleted(false);
        participantDAO.setJoinedAt(LocalDateTime.now());

        ChallengeParticipant participant = new ChallengeParticipant();
        participant.setUserId(userId);
        participant.setChallengeId(challengeId);

        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(userId);
        when(participantRepository.findByUserIdAndChallengeId(anyLong(), anyLong())).thenReturn(List.of());
        when(participantRepository.save(any(ChallengeParticipantDAO.class))).thenReturn(participantDAO);
        when(participantMapper.challengeParticipantDAOToChallengeParticipant(any())).thenReturn(participant);

        // Act
        ChallengeParticipant result = participantService.joinChallenge(challengeId, token);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(challengeId, result.getChallengeId());
        verify(participantRepository, times(1)).save(any(ChallengeParticipantDAO.class));
    }

    @Test
    void joinChallenge_ShouldThrowException_WhenUserAlreadyJoined() {
        // Arrange
        Long challengeId = 1L;
        String token = "Bearer validToken";
        Long userId = 2L;

        ChallengeParticipantDAO participantDAO = new ChallengeParticipantDAO();
        participantDAO.setUserId(userId);
        participantDAO.setChallengeId(challengeId);

        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(userId);
        when(participantRepository.findByUserIdAndChallengeId(anyLong(), anyLong())).thenReturn(List.of(participantDAO));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> participantService.joinChallenge(challengeId, token));
    }

    @Test
    void getParticipants_ShouldReturnListOfParticipants() {
        // Arrange
        Long challengeId = 1L;
        ChallengeParticipantDAO participantDAO = new ChallengeParticipantDAO();
        participantDAO.setChallengeId(challengeId);

        ChallengeParticipant participant = new ChallengeParticipant();
        participant.setChallengeId(challengeId);

        when(participantRepository.findByChallengeId(anyLong())).thenReturn(List.of(participantDAO));
        when(participantMapper.challengeParticipantDAOToChallengeParticipant(any())).thenReturn(participant);

        // Act
        List<ChallengeParticipant> result = participantService.getParticipants(challengeId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(challengeId, result.get(0).getChallengeId());
    }

    @Test
    void updateProgress_ShouldUpdateProgress_WhenUserIsAuthorized() {
        // Arrange
        Long challengeId = 1L;
        String token = "Bearer validToken";
        Long userId = 2L;

        ChallengeParticipantUpdateDTO updateDTO = new ChallengeParticipantUpdateDTO();
        updateDTO.setProgress(80);

        ChallengeParticipantDAO participantDAO = new ChallengeParticipantDAO();
        participantDAO.setUserId(userId);
        participantDAO.setChallengeId(challengeId);
        participantDAO.setProgress(50);

        ChallengeParticipant updatedParticipant = new ChallengeParticipant();
        updatedParticipant.setUserId(userId);
        updatedParticipant.setChallengeId(challengeId);
        updatedParticipant.setProgress(80);

        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(userId);
        when(jwtTokenUtil.extractRole(anyString())).thenReturn("USER");
        when(participantRepository.findByUserIdAndChallengeId(anyLong(), anyLong())).thenReturn(List.of(participantDAO));
        when(participantRepository.save(any(ChallengeParticipantDAO.class))).thenReturn(participantDAO);
        when(participantMapper.challengeParticipantDAOToChallengeParticipant(any())).thenReturn(updatedParticipant);

        // Act
        ChallengeParticipant result = participantService.updateProgress(challengeId, updateDTO, token);

        // Assert
        assertNotNull(result);
        assertEquals(80, result.getProgress());
    }

    @Test
    void updateProgress_ShouldThrowException_WhenUserNotAuthorized() {
        // Arrange
        Long challengeId = 1L;
        String token = "Bearer validToken";
        Long userId = 2L;

        ChallengeParticipantUpdateDTO updateDTO = new ChallengeParticipantUpdateDTO();
        updateDTO.setProgress(80);

        ChallengeParticipantDAO participantDAO = new ChallengeParticipantDAO();
        participantDAO.setUserId(3L);  // Different user ID
        participantDAO.setChallengeId(challengeId);

        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(userId);
        when(participantRepository.findByUserIdAndChallengeId(anyLong(), anyLong())).thenReturn(List.of(participantDAO));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> participantService.updateProgress(challengeId, updateDTO, token));
    }

    @Test
    void leaveChallenge_ShouldRemoveParticipant_WhenUserIsAuthorized() {
        // Arrange
        Long challengeId = 1L;
        String token = "Bearer validToken";
        Long userId = 2L;

        ChallengeParticipantDAO participantDAO = new ChallengeParticipantDAO();
        participantDAO.setUserId(userId);
        participantDAO.setChallengeId(challengeId);

        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(userId);
        when(jwtTokenUtil.extractRole(anyString())).thenReturn("USER");
        when(participantRepository.findByUserIdAndChallengeId(anyLong(), anyLong())).thenReturn(List.of(participantDAO));

        // Act
        participantService.leaveChallenge(challengeId, token);

        // Assert
        verify(participantRepository, times(1)).delete(any(ChallengeParticipantDAO.class));
    }

    @Test
    void leaveChallenge_ShouldThrowException_WhenUserIsNotAuthorized() {
        // Arrange
        Long challengeId = 1L;
        String token = "Bearer validToken";
        Long userId = 2L;

        ChallengeParticipantDAO participantDAO = new ChallengeParticipantDAO();
        participantDAO.setUserId(3L);  // Different user ID
        participantDAO.setChallengeId(challengeId);

        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(userId);
        when(participantRepository.findByUserIdAndChallengeId(anyLong(), anyLong())).thenReturn(List.of(participantDAO));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> participantService.leaveChallenge(challengeId, token));
    }
}
