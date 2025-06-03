package com.example.HeattKeeper.controllers;

import com.example.HeattKeeper.dal.DataAccessLayer;
import com.example.HeattKeeper.dto.*;
import com.example.HeattKeeper.models.User;
import com.example.HeattKeeper.service.ChatService;
import com.example.HeattKeeper.service.RelationshipAIService;
import com.example.HeattKeeper.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    private final UserDetailsServiceImpl userDetailsService;
    private final RelationshipAIService relationshipAIService;
    private final ChatService chatService;

    @Autowired
    public AIController(UserDetailsServiceImpl userDetailsService,
                        RelationshipAIService relationshipAIService,
                        ChatService chatService) {
        this.userDetailsService = userDetailsService;
        this.relationshipAIService = relationshipAIService;
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<List<MessageDTO>> chatWithAI(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AIChatRequest request) {
        try {
            // Get the authenticated user
            User user = userDetailsService.loadUserEntityByUsername(userDetails.getUsername());

            // Send user message to AI
            MessageDTO userMessage = chatService.sendMessage(
                    user.getEmail(),  // Using email as username
                    "AI",
                    request.getMessage(),
                    null
            );

            // Get the full conversation history
            List<MessageDTO> conversation = chatService.getMessages(user.getEmail(), "AI");

            return ResponseEntity.ok(conversation);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.emptyList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @PostMapping("/recommendations")
    public ResponseEntity<AIResponse> getAIRecommendation(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AIRecommendationRequest request) {
        try {
            // Get the authenticated user
            User user = userDetailsService.loadUserEntityByUsername(userDetails.getUsername());

            // Get AI recommendation
            AIResponse response = relationshipAIService.getAIResponse(
                    user.getId(),
                    request.getType(),
                    "recommendation"
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AIResponse("Error: " + e.getMessage(), null, null, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AIResponse("Internal server error", null, null, null));
        }
    }


}

