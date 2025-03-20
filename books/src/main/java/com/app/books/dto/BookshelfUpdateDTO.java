package com.app.books.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class BookshelfUpdateDTO {
    @NotBlank(message = "Status cannot be empty")
    @Pattern(regexp = "^(READING|READ|WANT_TO_READ)$", message = "Status must be either READ, READING, or WANT_TO_READ")
    private String status;
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
