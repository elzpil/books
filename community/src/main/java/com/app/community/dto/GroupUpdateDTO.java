package com.app.community.dto;

import com.app.community.model.PrivacySetting;
import jakarta.validation.constraints.NotBlank;

public class GroupUpdateDTO {

    private String name;
    private String description;
    private PrivacySetting privacySetting;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PrivacySetting getPrivacySetting() {
        return privacySetting;
    }

    public void setPrivacySetting(PrivacySetting privacySetting) {
        this.privacySetting = privacySetting;
    }
}
