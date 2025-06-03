package com.example.HeattKeeper.dto;

import lombok.Data;

@Data
public class PartnerDTO {
    private String username;
    private String email;
    private String profilePhotoUrl;


    public PartnerDTO(String username, String email, String profilePhotoUrl) {
        this.username = username;
        this.email = email;
        this.profilePhotoUrl = profilePhotoUrl;

    }
    public String getName() {
        return username;
    }
    public void setName(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }




}