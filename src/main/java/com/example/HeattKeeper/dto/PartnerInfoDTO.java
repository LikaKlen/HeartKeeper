package com.example.HeattKeeper.dto;

import lombok.Data;

@Data
public class PartnerInfoDTO {
    private boolean hasPartner;
    private String invitationCode; // null if hasPartner is true
    private PartnerDTO partner; // null if hasPartner is false

    public PartnerInfoDTO(boolean hasPartner, String invitationCode, PartnerDTO partner) {
        this.hasPartner = hasPartner;
        this.invitationCode = invitationCode;
        this.partner = partner;
    }
    public boolean isHasPartner() {
        return hasPartner;
    }

    public void setHasPartner(boolean hasPartner) {
        this.hasPartner = hasPartner;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public PartnerDTO getPartner() {
        return partner;
    }

    public void setPartner(PartnerDTO partner) {
        this.partner = partner;
    }
}