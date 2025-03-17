package com.app.community.controller;

import com.app.community.business.service.ChallengeParticipantService;
import com.app.community.model.ChallengeParticipant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChallengeParticipantControllerTest {

    @Mock
    private ChallengeParticipantService participantService;

    @InjectMocks
    private ChallengeParticipantController participantController;

    private ChallengeParticipant participant;

    @BeforeEach
    void setUp() {
        participant = new ChallengeParticipant();
        participant.setParticipantId(1L);
        participant.setUserId(100L);
        participant.setChallengeId(200L);
        participant.setProgress(50);
        participant.setCompleted(false);
        participant.setJoinedAt(LocalDateTime.now());
    }

    @Test
    void joinChallenge_ShouldReturnParticipant() {
        when(participantService.joinChallenge(200L, 100L)).thenReturn(participant);

        ResponseEntity<ChallengeParticipant> response = participantController.joinChallenge(200L, 100L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(participant);
        verify(participantService).joinChallenge(200L, 100L);
    }

    @Test
    void getParticipants_ShouldReturnListOfParticipants() {
        when(participantService.getParticipants(200L)).thenReturn(List.of(participant));

        ResponseEntity<List<ChallengeParticipant>> response = participantController.getParticipants(200L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull().hasSize(1);
        verify(participantService).getParticipants(200L);
    }

    @Test
    void updateProgress_ShouldReturnUpdatedParticipant() {
        when(participantService.updateProgress(200L, 100L, 80)).thenReturn(participant);

        ResponseEntity<ChallengeParticipant> response = participantController.updateProgress(200L, 100L, 80);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(participant);
        verify(participantService).updateProgress(200L, 100L, 80);
    }

    @Test
    void leaveChallenge_ShouldReturnNoContent() {
        doNothing().when(participantService).leaveChallenge(200L, 100L);

        ResponseEntity<Void> response = participantController.leaveChallenge(200L, 100L);

        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        verify(participantService).leaveChallenge(200L, 100L);
    }
}
