package com.example.HeattKeeper.service;

import com.example.HeattKeeper.dal.DataAccessLayer;
import com.example.HeattKeeper.dto.GoalDTO;
import com.example.HeattKeeper.dto.GoalStageDTO;
import com.example.HeattKeeper.models.CommonGoal;
import com.example.HeattKeeper.models.GoalStage;
import com.example.HeattKeeper.models.GoalType;
import com.example.HeattKeeper.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoalService {
    private DataAccessLayer dataAccessLayer;

    @Autowired
    public GoalService(DataAccessLayer dataAccessLayer) {
        this.dataAccessLayer = dataAccessLayer;
    }

    public List<GoalDTO> getPersonalGoals(String email) {
        User user = getUserByEmail(email);
        List<CommonGoal> goals = dataAccessLayer.getPersonalGoals(String.valueOf(user.getId()));
        return goals.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<GoalDTO> getSharedGoals(String email) {
        User user = getUserByEmail(email);
        List<CommonGoal> goals = dataAccessLayer.getSharedGoals(String.valueOf(user.getId()));
        return goals.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public GoalDTO createGoal(String email, GoalType type, GoalDTO dto) {
        User owner = getUserByEmail(email);
        CommonGoal goal = new CommonGoal();

        goal.setTitle(dto.getTitle());
        goal.setDescription(dto.getDescription());
        goal.setStartDate(dto.getStartDate());
        goal.setDeadline(dto.getDeadline());
        goal.setOwner(owner);

        if (type == GoalType.SHARED) {
            if (owner.getPartner() == null) {
                throw new RuntimeException("Cannot create shared goal without partner");
            }
            goal.setPartner(owner.getPartner());
        }

        // Save the goal first to generate ID
        goal = dataAccessLayer.createGoal(goal);

        // Then create and save stages
        if (dto.getStages() != null) {
            CommonGoal finalGoal = goal;
            List<GoalStage> stages = dto.getStages().stream().map(stageDTO -> {
                GoalStage stage = new GoalStage();
                stage.setDescription(stageDTO.getDescription());
                stage.setCompleted(stageDTO.isCompleted());
                stage.setGoal(finalGoal);//хуйня ебанная может поломать
                return stage;
            }).collect(Collectors.toList());

            goal.setStages(stages);
            goal = dataAccessLayer.updateGoal(goal); // Update with stages
        }

        return convertToDTO(goal);
    }

    public GoalDTO updateGoal(String email, GoalType type, Long id, GoalDTO dto) {
        User user = getUserByEmail(email);
        CommonGoal goal = getGoalIfAuthorized(id, user);

        goal.setTitle(dto.getTitle());
        goal.setDescription(dto.getDescription());
        goal.setStartDate(dto.getStartDate());
        goal.setDeadline(dto.getDeadline());

        // Update existing stages or create new ones
        if (dto.getStages() != null) {
            List<GoalStage> updatedStages = new ArrayList<>();
            for (GoalStageDTO stageDTO : dto.getStages()) {
                GoalStage stage = new GoalStage();
                if (stageDTO.getId() != null) {
                    // Find existing stage in the goal's stages
                    stage = goal.getStages().stream()
                            .filter(s -> s.getId().equals(stageDTO.getId()))
                            .findFirst()
                            .orElse(new GoalStage());
                }
                stage.setDescription(stageDTO.getDescription());
                stage.setCompleted(stageDTO.isCompleted());
                stage.setGoal(goal);
                updatedStages.add(stage);
            }

            // Update the goal with new stages
            goal.setStages(updatedStages);
        }

        goal = dataAccessLayer.updateGoal(goal);
        return convertToDTO(goal);
    }

    public void deleteGoal(String email, GoalType type, Long id) {
        User user = getUserByEmail(email);
        CommonGoal goal = getGoalIfAuthorized(id, user);
        dataAccessLayer.deleteGoal(goal.getId());
    }

    public GoalStageDTO updateStage(String email, GoalType type, Long goalId, Long stageId, Boolean completed) {
        User user = getUserByEmail(email);
        CommonGoal goal = getGoalIfAuthorized(goalId, user);

        GoalStage stage = goal.getStages().stream()
                .filter(s -> s.getId().equals(stageId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Stage not found"));

        stage.setCompleted(completed);
        goal = dataAccessLayer.updateGoal(goal);

        return new GoalStageDTO(stage.getId(), stage.getDescription(), stage.isCompleted());
    }

    private User getUserByEmail(String email) {
        return dataAccessLayer.getUserFromDatabaseByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private CommonGoal getGoalIfAuthorized(Long goalId, User user) {
        CommonGoal goal = dataAccessLayer.getGoalById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        if (!goal.getOwner().getId().equals(user.getId()) &&
                (goal.getPartner() == null || !goal.getPartner().getId().equals(user.getId()))) {
            throw new RuntimeException("User is not authorized to access this goal");
        }

        return goal;
    }

    private GoalDTO convertToDTO(CommonGoal goal) {
        GoalDTO dto = new GoalDTO();
        dto.setId(goal.getId());
        dto.setTitle(goal.getTitle());
        dto.setDescription(goal.getDescription());
        dto.setStartDate(goal.getStartDate());
        dto.setDeadline(goal.getDeadline());

        List<GoalStageDTO> stageDTOs = goal.getStages() != null
                ? goal.getStages().stream()
                .map(stage -> new GoalStageDTO(
                        stage.getId(),
                        stage.getDescription(),
                        stage.isCompleted()))
                .collect(Collectors.toList())
                : Collections.emptyList();

        dto.setStages(stageDTOs);

        long completedStages = stageDTOs.stream()
                .filter(GoalStageDTO::isCompleted)
                .count();

        dto.setProgress(stageDTOs.isEmpty() ? 0 :
                (int) ((double) completedStages / stageDTOs.size() * 100));

        return dto;
    }
}