package com.example.HeattKeeper.controllers;


import com.example.HeattKeeper.dal.DataAccessLayer;
import com.example.HeattKeeper.dto.MessageDTO;
import com.example.HeattKeeper.dto.MessageRequest;
import com.example.HeattKeeper.dto.PartnerStatusDTO;
import com.example.HeattKeeper.models.Chat;
import com.example.HeattKeeper.models.Message;
import com.example.HeattKeeper.models.User;
import com.example.HeattKeeper.security.UserDetailsImpl;
import com.example.HeattKeeper.service.ChatService;
import com.example.HeattKeeper.service.FileStorageService;
import com.example.HeattKeeper.service.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final FileStorageService fileStorageService;
    private final PartnerService partnerService;
    private final ChatService chatService;

    @Autowired
    public ChatController(
                          FileStorageService fileStorageService,
                          PartnerService partnerService,
                          ChatService chatService) {

        this.fileStorageService = fileStorageService;
        this.partnerService = partnerService;
        this.chatService = chatService;
    }

    @GetMapping("/messages")
    public List<MessageDTO> getMessages(@AuthenticationPrincipal UserDetails user,
                                        @RequestParam(required = false) String chatType) {
        String target = "AI"; // Default to AI chat
        if ("partner".equals(chatType)) {
            target = partnerService.getPartnerUsername(user.getUsername());
        }
        return chatService.getMessages(user.getUsername(), target);
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody MessageRequest request,
            @RequestParam(required = false) MultipartFile media) {

        try {
            String mediaUrl = null;
            if (media != null && !media.isEmpty()) {
                mediaUrl = fileStorageService.storeFile(media);
            }

            MessageDTO message = chatService.sendMessage(
                    userDetails.getUsername(),
                    request.getTarget(),
                    request.getText(),
                    mediaUrl
            );

            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/partner-status")
    public ResponseEntity<PartnerStatusDTO> getPartnerStatus(
            @AuthenticationPrincipal UserDetails user) {
        PartnerStatusDTO status = partnerService.getPartnerStatus(user.getUsername());
        return ResponseEntity.ok(status);
    }
}
