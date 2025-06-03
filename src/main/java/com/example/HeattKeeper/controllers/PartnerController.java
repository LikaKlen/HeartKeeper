package com.example.HeattKeeper.controllers;

import com.example.HeattKeeper.dto.ConnectRequest;
import com.example.HeattKeeper.dto.InvitationCodeDTO;
import com.example.HeattKeeper.dto.PartnerDTO;
import com.example.HeattKeeper.dto.PartnerInfoDTO;
import com.example.HeattKeeper.service.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/partner")
public class PartnerController {
    private final PartnerService partnerService;

    @Autowired
    public PartnerController(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    @GetMapping("/info")
    public ResponseEntity<PartnerInfoDTO> getPartnerInfo(@AuthenticationPrincipal UserDetails principal) {
        PartnerInfoDTO partnerInfo = partnerService.getPartnerInfo(principal.getUsername());
        return ResponseEntity.ok(partnerInfo);
    }

    @GetMapping("/code")
    public ResponseEntity<InvitationCodeDTO> getInvitationCode(@AuthenticationPrincipal UserDetails principal) {
        InvitationCodeDTO code = partnerService.getInvitationCode(principal.getUsername());
        return ResponseEntity.ok(code);
    }

    @PostMapping("/connect")
    public ResponseEntity<?> connectWithPartner(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody ConnectRequest request) {
        try {
            PartnerDTO partnerDTO = partnerService.connectWithPartner(
                    principal.getUsername(),
                    request.getCode()
            );
            return ResponseEntity.ok(partnerDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/disconnect")
    public ResponseEntity<Void> removePartner(@AuthenticationPrincipal UserDetails principal) {
        partnerService.removePartner(principal.getUsername());
        return ResponseEntity.noContent().build();
    }


}