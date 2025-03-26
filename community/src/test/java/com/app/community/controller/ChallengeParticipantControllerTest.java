package com.app.community.controller;

import com.app.community.business.service.ChallengeParticipantService;
import com.app.community.dto.ChallengeParticipantUpdateDTO;
import com.app.community.model.ChallengeParticipant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ChallengeParticipantControllerTest {

    @Mock
    private ChallengeParticipantService participantService;

    @InjectMocks
    private ChallengeParticipantController participantController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testJoinChallenge() {
        Long challengeId = 1L;
        String token = "Bearer token";

        ChallengeParticipant participant = new ChallengeParticipant();
        participant.setParticipantId(1L);
        participant.setUserId(100L);
        participant.setChallengeId(challengeId);
        participant.setProgress(0);
        participant.setCompleted(false);
        participant.setJoinedAt(LocalDateTime.now());

        when(participantService.joinChallenge(challengeId, token)).thenReturn(participant);

        ResponseEntity<ChallengeParticipant> response = participantController.joinChallenge(challengeId, token);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(participant, response.getBody());
        verify(participantService, times(1)).joinChallenge(challengeId, token);
    }

    @Test
    void testGetParticipants() {
        Long challengeId = 1L;

        ChallengeParticipant participant1 = new ChallengeParticipant();
        participant1.setParticipantId(1L);
        participant1.setUserId(100L);
        participant1.setChallengeId(challengeId);

        ChallengeParticipant participant2 = new ChallengeParticipant();
        participant2.setParticipantId(2L);
        participant2.setUserId(101L);
        participant2.setChallengeId(challengeId);

        when(participantService.getParticipants(challengeId)).thenReturn(List.of(participant1, participant2));

        ResponseEntity<List<ChallengeParticipant>> response = participantController.getParticipants(challengeId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(participantService, times(1)).getParticipants(challengeId);
    }

    @Test
    void testUpdateProgress() {
        Long challengeId = 1L;
        String token = "Bearer token";

        ChallengeParticipantUpdateDTO updateDTO = new ChallengeParticipantUpdateDTO();
        updateDTO.setProgress(50);


        ChallengeParticipant updatedParticipant = new ChallengeParticipant();
        updatedParticipant.setParticipantId(1L);
        updatedParticipant.setUserId(100L);
        updatedParticipant.setChallengeId(challengeId);
        updatedParticipant.setProgress(updateDTO.getProgress());


        when(participantService.updateProgress(eq(challengeId), any(ChallengeParticipantUpdateDTO.class), eq(token)))
                .thenReturn(updatedParticipant);

        ResponseEntity<ChallengeParticipant> response = participantController.updateProgress(challengeId, updateDTO, token);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(50, response.getBody().getProgress());
        verify(participantService, times(1)).updateProgress(eq(challengeId), any(ChallengeParticipantUpdateDTO.class), eq(token));
    }

    @Test
    void testLeaveChallenge() {
        Long challengeId = 1L;
        String token = "Bearer token";

        doNothing().when(participantService).leaveChallenge(challengeId, token);

        ResponseEntity<Void> response = participantController.leaveChallenge(challengeId, token);

        assertEquals(204, response.getStatusCodeValue());
        verify(participantService, times(1)).leaveChallenge(challengeId, token);
    }
}
