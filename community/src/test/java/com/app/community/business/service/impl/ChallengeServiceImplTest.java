package com.app.community.business.service.impl;

import com.app.community.auth.util.JwtTokenUtil;
import com.app.community.business.mapper.ChallengeMapper;
import com.app.community.business.repository.ChallengeRepository;
import com.app.community.business.repository.model.ChallengeDAO;
import com.app.community.business.service.ChallengeService;
import com.app.community.dto.ChallengeUpdateDTO;
import com.app.community.exception.ResourceNotFoundException;
import com.app.community.exception.UnauthorizedException;
import com.app.community.model.Challenge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceImplTest {

    @Mock
    private ChallengeRepository challengeRepository;

    @Mock
    private ChallengeMapper challengeMapper;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private ChallengeServiceImpl challengeService;

    private Challenge challenge;
    private ChallengeDAO challengeDAO;
    private final String token = "Bearer valid_token";
    private final Long userId = 1L;
    private final Long challengeId = 10L;

    @BeforeEach
    void setUp() {
        challenge = new Challenge();
        challenge.setId(challengeId);
        challenge.setName("Read 10 Books");
        challenge.setDescription("A challenge to read 10 books in a year.");
        challenge.setCreatorId(userId);
        challenge.setStartDate(LocalDate.now());
        challenge.setEndDate(LocalDate.now().plusMonths(6));

        challengeDAO = new ChallengeDAO();
        challengeDAO.setId(challengeId);
        challengeDAO.setName("Read 10 Books");
        challengeDAO.setDescription("A challenge to read 10 books in a year.");
        challengeDAO.setCreatorId(userId);
        challengeDAO.setStartDate(LocalDate.now());
        challengeDAO.setEndDate(LocalDate.now().plusMonths(6));
    }

    @Test
    void createChallenge_ShouldReturnCreatedChallenge() {
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(userId);
        when(challengeMapper.challengeToChallengeDAO(any(Challenge.class))).thenReturn(challengeDAO);
        when(challengeRepository.save(any(ChallengeDAO.class))).thenReturn(challengeDAO);
        when(challengeMapper.challengeDAOToChallenge(any(ChallengeDAO.class))).thenReturn(challenge);

        Challenge createdChallenge = challengeService.createChallenge(challenge, token);

        assertThat(createdChallenge).isNotNull();
        assertThat(createdChallenge.getName()).isEqualTo("Read 10 Books");
        verify(challengeRepository, times(1)).save(any(ChallengeDAO.class));
    }

    @Test
    void getAllChallenges_ShouldReturnListOfChallenges() {
        when(challengeRepository.findAll()).thenReturn(List.of(challengeDAO));
        when(challengeMapper.challengeDAOToChallenge(any(ChallengeDAO.class))).thenReturn(challenge);

        List<Challenge> challenges = challengeService.getAllChallenges();

        assertThat(challenges).hasSize(1);
        assertThat(challenges.get(0).getName()).isEqualTo("Read 10 Books");
    }

    @Test
    void getChallengeById_ShouldReturnChallenge_WhenFound() {
        when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challengeDAO));
        when(challengeMapper.challengeDAOToChallenge(any(ChallengeDAO.class))).thenReturn(challenge);

        Optional<Challenge> foundChallenge = challengeService.getChallengeById(challengeId);

        assertThat(foundChallenge).isPresent();
        assertThat(foundChallenge.get().getName()).isEqualTo("Read 10 Books");
    }

    @Test
    void getChallengeById_ShouldReturnEmpty_WhenNotFound() {
        when(challengeRepository.findById(challengeId)).thenReturn(Optional.empty());

        Optional<Challenge> foundChallenge = challengeService.getChallengeById(challengeId);

        assertThat(foundChallenge).isEmpty();
    }

    @Test
    void updateChallenge_ShouldUpdateAndReturnChallenge_WhenAuthorized() {
        ChallengeUpdateDTO updateDTO = new ChallengeUpdateDTO();
        updateDTO.setName("Updated Challenge");

        when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challengeDAO));
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(userId);
        when(jwtTokenUtil.extractRole(anyString())).thenReturn("USER");

        // Simulate updated ChallengeDAO
        ChallengeDAO updatedChallengeDAO = new ChallengeDAO();
        updatedChallengeDAO.setId(challengeId);
        updatedChallengeDAO.setName(updateDTO.getName()); // Updated name
        updatedChallengeDAO.setDescription(challengeDAO.getDescription());
        updatedChallengeDAO.setCreatorId(challengeDAO.getCreatorId());
        updatedChallengeDAO.setStartDate(challengeDAO.getStartDate());
        updatedChallengeDAO.setEndDate(challengeDAO.getEndDate());

        when(challengeRepository.save(any(ChallengeDAO.class))).thenReturn(updatedChallengeDAO);

        // Simulate mapping of updated DAO to Challenge model
        Challenge updatedChallenge = new Challenge();
        updatedChallenge.setId(challengeId);
        updatedChallenge.setName(updateDTO.getName()); // Updated name
        updatedChallenge.setDescription(challenge.getDescription());
        updatedChallenge.setCreatorId(challenge.getCreatorId());
        updatedChallenge.setStartDate(challenge.getStartDate());
        updatedChallenge.setEndDate(challenge.getEndDate());

        when(challengeMapper.challengeDAOToChallenge(updatedChallengeDAO)).thenReturn(updatedChallenge);

        Challenge result = challengeService.updateChallenge(challengeId, updateDTO, token);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Challenge"); // Check if the name was updated
        verify(challengeRepository, times(1)).save(any(ChallengeDAO.class));
    }


    @Test
    void updateChallenge_ShouldThrowUnauthorizedException_WhenUnauthorized() {
        ChallengeUpdateDTO updateDTO = new ChallengeUpdateDTO();
        updateDTO.setName("Updated Challenge");

        when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challengeDAO));
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(99L); // Unauthorized user
        when(jwtTokenUtil.extractRole(anyString())).thenReturn("USER");

        assertThatThrownBy(() -> challengeService.updateChallenge(challengeId, updateDTO, token))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("You are not authorized to update this challenge");
    }

    @Test
    void deleteChallenge_ShouldDeleteChallenge_WhenAuthorized() {
        when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challengeDAO));
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(userId);
        when(jwtTokenUtil.extractRole(anyString())).thenReturn("USER");

        challengeService.deleteChallenge(challengeId, token);

        verify(challengeRepository, times(1)).deleteById(challengeId);
    }

    @Test
    void deleteChallenge_ShouldThrowUnauthorizedException_WhenUnauthorized() {
        when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challengeDAO));
        when(jwtTokenUtil.extractUserId(anyString())).thenReturn(99L); // Unauthorized user
        when(jwtTokenUtil.extractRole(anyString())).thenReturn("USER");

        assertThatThrownBy(() -> challengeService.deleteChallenge(challengeId, token))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("You are not authorized to update this challenge");
    }

    @Test
    void searchChallenges_ShouldReturnMatchingChallenges() {
        when(challengeRepository.searchChallenges("Read", "Books")).thenReturn(List.of(challengeDAO));
        when(challengeMapper.challengeDAOToChallenge(any(ChallengeDAO.class))).thenReturn(challenge);

        List<Challenge> challenges = challengeService.searchChallenges("Read", "Books");

        assertThat(challenges).hasSize(1);
        assertThat(challenges.get(0).getName()).isEqualTo("Read 10 Books");
    }

    @Test
    void getChallengesSortedByPopularity_ShouldReturnSortedChallenges() {
        when(challengeRepository.findChallengesSortedByPopularity()).thenReturn(List.of(challengeDAO));
        when(challengeMapper.challengeDAOToChallenge(any(ChallengeDAO.class))).thenReturn(challenge);

        List<Challenge> challenges = challengeService.getChallengesSortedByPopularity();

        assertThat(challenges).hasSize(1);
        assertThat(challenges.get(0).getName()).isEqualTo("Read 10 Books");
    }
}
