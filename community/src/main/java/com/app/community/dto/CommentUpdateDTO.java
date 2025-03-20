package com.app.community.dto;

import jakarta.validation.constraints.Size;

public class CommentUpdateDTO {

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
