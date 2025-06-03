package com.example.HeattKeeper.dto;

import lombok.Data;

@Data
public class PartnerStatusDTO {
    private boolean hasPartner;
    private String partnerUsername;
    private String partnerAvatar;

    public PartnerStatusDTO(boolean hasPartner, String partnerUsername, String partnerAvatar) {
        this.hasPartner = hasPartner;
        this.partnerUsername = partnerUsername;
        this.partnerAvatar = partnerAvatar;
    }

    public boolean isHasPartner() {
        return hasPartner;
    }

    public void setHasPartner(boolean hasPartner) {
        this.hasPartner = hasPartner;
    }

    public String getPartnerUsername() {
        return partnerUsername;
    }

    public void setPartnerUsername(String partnerUsername) {
        this.partnerUsername = partnerUsername;
    }

    public String getPartnerAvatar() {
        return partnerAvatar;
    }

    public void setPartnerAvatar(String partnerAvatar) {
        this.partnerAvatar = partnerAvatar;
    }
// getters and setters
}