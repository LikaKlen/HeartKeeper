package com.example.HeattKeeper.controllers;

import com.example.HeattKeeper.dal.DataAccessLayer;
import com.example.HeattKeeper.dto.EditUser;
import com.example.HeattKeeper.models.Theme;
import com.example.HeattKeeper.models.User;
import com.example.HeattKeeper.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")

public class UserController {
    private final DataAccessLayer dataAccessLayer;

    private final UserDetailsServiceImpl userService;

    @Autowired
    public UserController(UserDetailsServiceImpl userService,DataAccessLayer dataAccessLayer) {
        this.userService = userService;
        this.dataAccessLayer = dataAccessLayer;
    }
    @GetMapping("")
    public ResponseEntity getUser() {
        return ResponseEntity.ok(dataAccessLayer.getUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity getUserById(@PathVariable("id") long id) {
        return ResponseEntity.ok(dataAccessLayer.getUserId(id));
    }
    @PostMapping("/{userId}/assign-partner")
    public ResponseEntity<User> assignPartner(@PathVariable Long userId, @RequestParam String invitationCode) {
        User user = userService.assignPartner(userId, invitationCode);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/theme")
    public ResponseEntity<?> updateTheme(@RequestBody Map<String, String> body, Principal principal) {
        String email = principal.getName();
        Theme newTheme = Theme.valueOf(body.get("theme"));

        Optional<User> userOpt = dataAccessLayer.getUserFromDatabaseByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setTheme(newTheme);

            boolean success = dataAccessLayer.updateUser(user);
            if (success) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update theme");
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    @GetMapping("/user/theme")
    public ResponseEntity<Map<String, String>> getTheme(Principal principal) {
        String email = principal.getName();
        return dataAccessLayer.getUserFromDatabaseByEmail(email)
                .map(user -> ResponseEntity.ok(Map.of("theme", user.getTheme().toString())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found")));
    }
}