package com.app.community.business.repository.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "challenge_participants")
public class ChallengeParticipantDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id", nullable = false)
    private Long participantId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "challenge_id", nullable = false)
    private Long challengeId;

    @Column(nullable = false)
    private int progress;

    @Column(nullable = false)
    private boolean completed;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    public ChallengeParticipantDAO() {}

    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }

    public Long getUser() { return userId; }
    public void setUser(Long userId) { this.userId = userId; }

    public Long getChallenge() { return challengeId; }
    public void setChallenge(Long challengeId) { this.challengeId = challengeId; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }

    @Override
    public String toString() {
        return "ChallengeParticipantDAO{" +
                "participantId=" + participantId +
                ", userId=" + userId +
                ", challengeId=" + challengeId +
                ", progress=" + progress +
                ", completed=" + completed +
                ", joinedAt=" + joinedAt +
                '}';
    }

}
