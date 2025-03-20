package com.app.books.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class ReadingProgressUpdateDTO {
    @Min(value = 0, message = "Percentage read must be at least 0")
    @Max(value = 100, message = "Percentage read cannot exceed 100")
    private Integer percentageRead;

    public Integer getPercentageRead() {
        return percentageRead;
    }

    public void setPercentageRead(Integer percentageRead) {
        this.percentageRead = percentageRead;
    }
}
