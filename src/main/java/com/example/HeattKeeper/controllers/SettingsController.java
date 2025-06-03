package com.example.HeattKeeper.controllers;

import com.example.HeattKeeper.dto.SettingsDto;
import com.example.HeattKeeper.dto.SupportRequestDto;
import com.example.HeattKeeper.models.Settings;
import com.example.HeattKeeper.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {
    private final SettingsService settingsService;

    @GetMapping
    public ResponseEntity<Settings> getSettings(@RequestParam Long userId) {
        return ResponseEntity.ok(settingsService.getSettings(userId));
    }

    @PutMapping
    public ResponseEntity<Settings> updateSettings(@RequestParam Long userId, @RequestBody SettingsDto dto) {
        return ResponseEntity.ok(settingsService.updateSettings(userId, dto));
    }

    @PostMapping("/support")
    public ResponseEntity<Void> sendSupportRequest(@RequestBody SupportRequestDto dto) {
        settingsService.sendSupportRequest( dto);
        return ResponseEntity.ok().build();
    }
}