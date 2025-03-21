package com.app.community.dto;

import jakarta.validation.constraints.NotBlank;

public class GroupMembershipUpdateDTO {
    @NotBlank(message = "Role id is required")
    private String role;   // 'admin', 'moderator', 'member'

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
