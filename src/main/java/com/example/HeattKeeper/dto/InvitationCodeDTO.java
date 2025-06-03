package com.example.HeattKeeper.dto;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
public class InvitationCodeDTO {
    private String code;
    // Add constructor
    public InvitationCodeDTO(String code) {
        this.code = code;
    }

    // Getters and setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}