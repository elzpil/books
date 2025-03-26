package com.app.community.controller;

import com.app.community.business.service.ChallengeService;
import com.app.community.dto.ChallengeUpdateDTO;
import com.app.community.model.Challenge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        challenge.setName("Read 10 Books");
        challenge.setDescription("A challenge to read 10 books in a month");
        challenge.setCreatorId(100L);
        challenge.setStartDate(LocalDate.now());
        challenge.setEndDate(LocalDate.now().plusDays(30));
    }

    @Test
    void testCreateChallenge() {
        when(challengeService.createChallenge(any(Challenge.class), any(String.class))).thenReturn(challenge);
        ResponseEntity<Challenge> response = challengeController.createChallenge(challenge, "Bearer token");
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(challenge, response.getBody());
    }

    @Test
    void testGetAllChallenges() {
        when(challengeService.getAllChallenges()).thenReturn(List.of(challenge));
        ResponseEntity<List<Challenge>> response = challengeController.getAllChallenges();
        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void testGetChallengeById_Found() {
        when(challengeService.getChallengeById(1L)).thenReturn(Optional.of(challenge));
        ResponseEntity<Challenge> response = challengeController.getChallengeById(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(challenge, response.getBody());
    }

    @Test
    void testGetChallengeById_NotFound() {
        when(challengeService.getChallengeById(1L)).thenReturn(Optional.empty());
        ResponseEntity<Challenge> response = challengeController.getChallengeById(1L);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testUpdateChallenge() {
        ChallengeUpdateDTO updateDTO = new ChallengeUpdateDTO();
        updateDTO.setName("Updated Name");
        updateDTO.setDescription("Updated Description");
        updateDTO.setStartDate(LocalDate.now());
        updateDTO.setEndDate(LocalDate.now().plusDays(10));

        when(challengeService.updateChallenge(eq(1L), any(ChallengeUpdateDTO.class), any(String.class))).thenReturn(challenge);
        ResponseEntity<Challenge> response = challengeController.updateChallenge(1L, updateDTO, "Bearer token");
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testDeleteChallenge() {
        doNothing().when(challengeService).deleteChallenge(1L, "Bearer token");
        ResponseEntity<Void> response = challengeController.deleteChallenge(1L, "Bearer token");
        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void testSearchChallenges() {
        when(challengeService.searchChallenges("Read", null)).thenReturn(List.of(challenge));
        ResponseEntity<List<Challenge>> response = challengeController.searchChallenges("Read", null);
        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void testGetChallengesByPopularity() {
        when(challengeService.getChallengesSortedByPopularity()).thenReturn(List.of(challenge));
        ResponseEntity<List<Challenge>> response = challengeController.getChallengesByPopularity();
        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
    }
}
