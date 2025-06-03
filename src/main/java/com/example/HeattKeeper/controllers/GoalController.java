package com.example.HeattKeeper.controllers;

import com.example.HeattKeeper.dto.GoalDTO;
import com.example.HeattKeeper.dto.GoalStageDTO;
import com.example.HeattKeeper.models.GoalType;
import com.example.HeattKeeper.service.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import jakarta.validation.constraints.Email;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    @Autowired
    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping("/personal")
    public ResponseEntity<GoalDTO> createPersonalGoal(
            @Valid @RequestBody GoalDTO goalDTO,
            @RequestHeader("X-User-Email") @Email String email) {
        GoalDTO createdGoal = goalService.createGoal(email, GoalType.PERSONAL, goalDTO);
        return ResponseEntity.ok(createdGoal);
    }

    @PostMapping("/shared")
    public ResponseEntity<GoalDTO> createSharedGoal(
            @Valid @RequestBody GoalDTO goalDTO,
            @RequestHeader("X-User-Email") @Email String email) {
        GoalDTO createdGoal = goalService.createGoal(email, GoalType.SHARED, goalDTO);
        return ResponseEntity.ok(createdGoal);
    }

    @GetMapping("/personal")
    public ResponseEntity<List<GoalDTO>> getPersonalGoals(
            @RequestHeader("X-User-Email") @Email String email) {
        List<GoalDTO> goals = goalService.getPersonalGoals(email);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/shared")
    public ResponseEntity<List<GoalDTO>> getSharedGoals(
            @RequestHeader("X-User-Email") @Email String email) {
        List<GoalDTO> goals = goalService.getSharedGoals(email);
        return ResponseEntity.ok(goals);
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<GoalDTO> updateGoal(
            @PathVariable Long goalId,
            @RequestParam GoalType type,
            @Valid @RequestBody GoalDTO goalDTO,
            @RequestHeader("X-User-Email") @Email String email) {
        GoalDTO updated = goalService.updateGoal(email, type, goalId, goalDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(
            @PathVariable Long goalId,
            @RequestParam GoalType type,
            @RequestHeader("X-User-Email") @Email String email) {
        goalService.deleteGoal(email, type, goalId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{goalId}/stages/{stageId}")
    public ResponseEntity<GoalStageDTO> updateStageCompletion(
            @PathVariable Long goalId,
            @PathVariable Long stageId,
            @RequestParam GoalType type,
            @RequestParam Boolean completed,
            @RequestHeader("X-User-Email") @Email String email) {
        GoalStageDTO stageDTO = goalService.updateStage(email, type, goalId, stageId, completed);
        return ResponseEntity.ok(stageDTO);
    }
}

