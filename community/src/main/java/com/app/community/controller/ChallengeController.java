package com.app.community.controller;

import com.app.community.business.service.ChallengeService;
import com.app.community.dto.ChallengeUpdateDTO;
import com.app.community.model.Challenge;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @PostMapping
    public ResponseEntity<Challenge> createChallenge(@Valid @RequestBody Challenge challenge,
                                                     @RequestHeader("Authorization") String token) {
        log.info("Creating challenge: {}", challenge);
        Challenge createdChallenge = challengeService.createChallenge(challenge, token);
        return ResponseEntity.status(201).body(createdChallenge);
    }

    @GetMapping
    public ResponseEntity<List<Challenge>> getAllChallenges() {
        List<Challenge> challenges = challengeService.getAllChallenges();
        log.info("Fetched challenges");
        return ResponseEntity.ok(challenges);
    }

    @GetMapping("/{challengeId}")
    public ResponseEntity<Challenge> getChallengeById(@PathVariable Long challengeId) {
        Optional<Challenge> challenge = challengeService.getChallengeById(challengeId);
        return challenge.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{challengeId}")
    public ResponseEntity<Challenge> updateChallenge(@PathVariable Long challengeId,
                                                     @Valid @RequestBody ChallengeUpdateDTO challengeUpdateDTO,
                                                     @RequestHeader("Authorization") String token) {
        log.info("Updating challenge ID {}: {}", challengeId, challengeUpdateDTO);
        return ResponseEntity.ok(challengeService.updateChallenge(challengeId, challengeUpdateDTO, token));
    }

    @DeleteMapping("/{challengeId}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable Long challengeId,
                                                @RequestHeader("Authorization") String token) {
        log.info("Deleting challenge ID {}", challengeId);
        challengeService.deleteChallenge(challengeId, token);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Challenge>> searchChallenges(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description) {
        List<Challenge> challenges = challengeService.searchChallenges(name, description);
        return ResponseEntity.ok(challenges);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Challenge>> getChallengesByPopularity() {
        List<Challenge> challenges = challengeService.getChallengesSortedByPopularity();
        return ResponseEntity.ok(challenges);
    }

}
