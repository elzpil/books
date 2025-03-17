package com.app.community.model;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class ChallengeParticipant {
    private Long participantId;
    @NotBlank(message = "User id is required")
    private Long userId;
    @NotBlank(message = "Challenge id is required")
    private Long challengeId;
    private int progress;
    private boolean completed;
    private LocalDateTime joinedAt;

    public ChallengeParticipant() {}

    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getChallengeId() { return challengeId; }
    public void setChallengeId(Long challengeId) { this.challengeId = challengeId; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
