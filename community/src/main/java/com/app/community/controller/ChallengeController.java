package com.app.community.controller;

import com.app.community.business.service.ChallengeService;
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
    public ResponseEntity<Challenge> createChallenge(@Valid @RequestBody Challenge challenge) {
        log.info("Creating challenge: {}", challenge);
        Challenge createdChallenge = challengeService.createChallenge(challenge);
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
    public ResponseEntity<Challenge> updateChallenge(@PathVariable Long challengeId, @Valid @RequestBody Challenge challenge) {
        log.info("Updating challenge ID {}: {}", challengeId, challenge);
        return ResponseEntity.ok(challengeService.updateChallenge(challengeId, challenge));
    }

    @DeleteMapping("/{challengeId}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable Long challengeId) {
        log.info("Deleting challenge ID {}", challengeId);
        challengeService.deleteChallenge(challengeId);
        return ResponseEntity.noContent().build();
    }
}
