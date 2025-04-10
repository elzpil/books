
package com.app.community.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


public class GroupMembershipAddDTO {
    @NotNull
    private Long userId;

    @NotNull
    private String role; // e.g., "member" or "moderator"

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
