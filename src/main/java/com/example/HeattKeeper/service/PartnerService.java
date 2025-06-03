package com.example.HeattKeeper.service;

import com.example.HeattKeeper.dal.DataAccessLayer;
import com.example.HeattKeeper.dto.InvitationCodeDTO;
import com.example.HeattKeeper.dto.PartnerDTO;
import com.example.HeattKeeper.dto.PartnerInfoDTO;
import com.example.HeattKeeper.dto.PartnerStatusDTO;
import com.example.HeattKeeper.models.User;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PartnerService {
    private final DataAccessLayer dataAccessLayer;
    private final UserDetailsServiceImpl userDetailsService;
    private final ChatService chatService;

    private static final int INVITATION_CODE_LENGTH = 8;
    @Autowired
    public PartnerService(DataAccessLayer dataAccessLayer,
                          UserDetailsServiceImpl userDetailsService,
                          ChatService chatService) {
        this.dataAccessLayer = dataAccessLayer;
        this.userDetailsService = userDetailsService;
        this.chatService = chatService;
    }

    private String generateUniqueInvitationCode() {
        String code;
        do {
            code = generateRandomCode();
        } while (dataAccessLayer.findByInvitationCode(code).isPresent());
        return code;
    }

    public PartnerInfoDTO getPartnerInfo(String email) {
        User user = dataAccessLayer.getUserFromDatabaseByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean hasPartner = user.getPartner() != null;
        String invitationCode = user.getInvitationCode();

        if (!hasPartner && invitationCode == null) {
            invitationCode = generateUniqueInvitationCode();
            user.setInvitationCode(invitationCode);
            dataAccessLayer.save(user);
        }

        PartnerDTO partnerDto = null;
        if (hasPartner) {
            partnerDto = new PartnerDTO(
                    user.getPartner().getUsername(),
                    user.getPartner().getEmail(),
                    user.getPartner().getProfilePhotoUrl()
            );
        }

        return new PartnerInfoDTO(
                hasPartner,
                invitationCode,
                partnerDto
        );
    }

    public InvitationCodeDTO getInvitationCode(String email) {
        User user = dataAccessLayer.getUserFromDatabaseByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getPartner() != null) {
            throw new RuntimeException("You already have a partner");
        }

        if (user.getInvitationCode() == null) {
            String code = generateUniqueInvitationCode();
            user.setInvitationCode(code);
            dataAccessLayer.save(user);
            return new InvitationCodeDTO(code);
        } else {
            return new InvitationCodeDTO(user.getInvitationCode());
        }
    }

    public PartnerDTO connectWithPartner(String email, String partnerCode) {
        User user = dataAccessLayer.getUserFromDatabaseByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getPartner() != null) {
            throw new RuntimeException("You already have a partner");
        }

        User partner = dataAccessLayer.findByInvitationCode(partnerCode)
                .orElseThrow(() -> new RuntimeException("Partner with this code not found"));

        if (partner.getEmail().equals(email)) {
            throw new RuntimeException("You cannot add yourself as partner");
        }

        user.setPartner(partner);
        partner.setPartner(user);

        user.setInvitationCode(null);
        partner.setInvitationCode(null);

        dataAccessLayer.save(user);
        dataAccessLayer.save(partner);

        chatService.createChat(user, partner);

        return new PartnerDTO(
                partner.getUsername(),
                partner.getEmail(),
                partner.getProfilePhotoUrl()
        );
    }

    public void removePartner(String email) {
        User user = dataAccessLayer.getUserFromDatabaseByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getPartner() == null) {
            throw new RuntimeException("You don't have a partner");
        }

        User partner = user.getPartner();

        user.setPartner(null);
        partner.setPartner(null);

        user.setInvitationCode(generateUniqueInvitationCode());
        partner.setInvitationCode(generateUniqueInvitationCode());

        dataAccessLayer.save(user);
        dataAccessLayer.save(partner);
    }

    private String generateRandomCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < INVITATION_CODE_LENGTH; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    public String getPartnerUsername(String email) {
        User user = dataAccessLayer.getUserFromDatabaseByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getPartner() == null) {
            throw new RuntimeException("User has no partner");
        }

        return user.getPartner().getEmail();
    }

    public PartnerStatusDTO getPartnerStatus(String email) {
        User user = dataAccessLayer.getUserFromDatabaseByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean hasPartner = user.getPartner() != null;
        String partnerUsername = hasPartner ? user.getPartner().getUsername() : null;
        String partnerAvatar = hasPartner ? user.getPartner().getProfilePhotoUrl() : null;

        return new PartnerStatusDTO(hasPartner, partnerUsername, partnerAvatar);
    }
}