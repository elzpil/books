package com.app.books.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
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
}
