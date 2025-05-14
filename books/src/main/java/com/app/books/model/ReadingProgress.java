package com.app.books.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
public class ReadingProgress {
    private Long id;

    private Long userId;

    private Long bookId;

    @Min(value = 0, message = "Percentage read must be at least 0")
    @Max(value = 100, message = "Percentage read cannot exceed 100")
    private Integer percentageRead;

    private LocalDateTime lastUpdated = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Integer getPercentageRead() {
        return percentageRead;
    }

    public void setPercentageRead(Integer percentageRead) {
        this.percentageRead = percentageRead;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
