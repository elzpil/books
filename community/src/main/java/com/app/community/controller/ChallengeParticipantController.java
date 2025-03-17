package com.app.community.controller;

import com.app.community.business.service.ChallengeParticipantService;
import com.app.community.model.ChallengeParticipant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/challenges/{challengeId}")
public class ChallengeParticipantController {

    private final ChallengeParticipantService participantService;

    public ChallengeParticipantController(ChallengeParticipantService participantService) {
        this.participantService = participantService;
    }

    @PostMapping("/join")
    public ResponseEntity<ChallengeParticipant> joinChallenge(@PathVariable Long challengeId, @RequestParam Long userId) {
        log.info("Joining challenge with id: {}", challengeId);
        return ResponseEntity.ok(participantService.joinChallenge(challengeId, userId));
    }

    @GetMapping("/participants")
    public ResponseEntity<List<ChallengeParticipant>> getParticipants(@PathVariable Long challengeId) {
        log.info("Getting participants of challenge: {}", challengeId);
        return ResponseEntity.ok(participantService.getParticipants(challengeId));
    }

    @PutMapping("/progress")
    public ResponseEntity<ChallengeParticipant> updateProgress(@PathVariable Long challengeId, @RequestParam Long userId, @RequestParam int progress) {
        log.info("Updating challenge progress: {}", challengeId);
        return ResponseEntity.ok(participantService.updateProgress(challengeId, userId, progress));
    }

    @DeleteMapping("/leave")
    public ResponseEntity<Void> leaveChallenge(@PathVariable Long challengeId, @RequestParam Long userId) {
        log.info("Leaving challenge id: {}", challengeId);
        participantService.leaveChallenge(challengeId, userId);
        return ResponseEntity.noContent().build();
    }
}
