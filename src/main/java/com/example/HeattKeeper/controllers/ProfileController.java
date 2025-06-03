package com.example.HeattKeeper.controllers;

import com.example.HeattKeeper.dto.EditUser;
import com.example.HeattKeeper.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }
    @PostMapping("/photo")
    public ResponseEntity<?> uploadProfilePhoto(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        String email = userDetails.getUsername();
        try {
            // Save image to disk or cloud storage (example saves to /uploads)
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get("uploads/" + filename);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = "/uploads/" + filename; // adjust URL logic if needed
            profileService.updateUserAvatar(email, imageUrl);

            return ResponseEntity.ok().body(Map.of("photoUrl", imageUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
    }
    @GetMapping
    public ResponseEntity<EditUser> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(profileService.getProfile(email));
    }

    @PutMapping
    public ResponseEntity<EditUser> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody EditUser profileDTO) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(profileService.updateProfile(email, profileDTO));
    }
}