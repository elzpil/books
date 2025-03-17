package com.app.community.controller;

import com.app.community.business.service.ChallengeService;
import com.app.community.model.Challenge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChallengeControllerTest {

    @Mock
    private ChallengeService challengeService;

    @InjectMocks
    private ChallengeController challengeController;

    private Challenge challenge;

    @BeforeEach
    void setUp() {
        challenge = new Challenge();
        challenge.setId(1L);
        challenge.setName("Test Challenge");
        challenge.setDescription("A sample challenge");
        challenge.setCreatorId(100L);
        challenge.setStartDate(LocalDate.now());
        challenge.setEndDate(LocalDate.now().plusDays(7));
    }

    @Test
    void createChallenge_ShouldReturnCreatedChallenge() {
        when(challengeService.createChallenge(any(Challenge.class))).thenReturn(challenge);

        ResponseEntity<Challenge> response = challengeController.createChallenge(challenge);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody()).isEqualTo(challenge);
        verify(challengeService).createChallenge(any(Challenge.class));
    }

    @Test
    void getAllChallenges_ShouldReturnListOfChallenges() {
        when(challengeService.getAllChallenges()).thenReturn(List.of(challenge));

        ResponseEntity<List<Challenge>> response = challengeController.getAllChallenges();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull().hasSize(1);
        verify(challengeService).getAllChallenges();
    }

    @Test
    void getChallengeById_ShouldReturnChallenge_WhenExists() {
        when(challengeService.getChallengeById(1L)).thenReturn(Optional.of(challenge));

        ResponseEntity<Challenge> response = challengeController.getChallengeById(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(challenge);
        verify(challengeService).getChallengeById(1L);
    }

    @Test
    void getChallengeById_ShouldReturnNotFound_WhenDoesNotExist() {
        when(challengeService.getChallengeById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Challenge> response = challengeController.getChallengeById(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        verify(challengeService).getChallengeById(1L);
    }

    @Test
    void updateChallenge_ShouldReturnUpdatedChallenge() {
        when(challengeService.updateChallenge(eq(1L), any(Challenge.class))).thenReturn(challenge);

        ResponseEntity<Challenge> response = challengeController.updateChallenge(1L, challenge);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(challenge);
        verify(challengeService).updateChallenge(eq(1L), any(Challenge.class));
    }

    @Test
    void deleteChallenge_ShouldReturnNoContent() {
        doNothing().when(challengeService).deleteChallenge(1L);

        ResponseEntity<Void> response = challengeController.deleteChallenge(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        verify(challengeService).deleteChallenge(1L);
    }
}
