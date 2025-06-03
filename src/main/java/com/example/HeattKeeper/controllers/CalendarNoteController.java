package com.example.HeattKeeper.controllers;

import com.example.HeattKeeper.dal.DataAccessLayer;
import com.example.HeattKeeper.models.CalendarNote;
import com.example.HeattKeeper.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarNoteController {

    private final DataAccessLayer dal;

    @PostMapping("/note")
    public ResponseEntity<?> createOrUpdateNote(@RequestBody CalendarNote note, @RequestParam Long userId) {
        Optional<User> userOpt = dal.findById(userId);
        if (userOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

        note.setCreatedBy(userOpt.get());
        dal.saveNote(note);
        return ResponseEntity.ok(note);
    }

    @GetMapping("/notes/{userId}")
    public ResponseEntity<List<CalendarNote>> getNotes(@PathVariable Long userId) {
        Optional<User> user = dal.findById(userId);
        return user.map(value -> ResponseEntity.ok(dal.getNotesForUser(value)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/note/{noteId}")
    public ResponseEntity<?> deleteNote(@PathVariable Long noteId, @RequestParam Long userId) {
        boolean deleted = dal.deleteNoteIfOwner(noteId, userId);
        return deleted ? ResponseEntity.ok("Deleted") : ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
    }

    @PatchMapping("/note/{noteId}/lock")
    public ResponseEntity<?> lockNote(@PathVariable Long noteId, @RequestParam Long userId, @RequestParam boolean locked) {
        boolean updated = dal.updateLockStatus(noteId, locked, userId);
        return updated ? ResponseEntity.ok("Updated") : ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not allowed");
    }

    @GetMapping("/note/{id}")
    public ResponseEntity<CalendarNote> getNote(@PathVariable Long id) {
        return dal.getNoteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
