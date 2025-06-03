package com.example.HeattKeeper.dto;

import com.example.HeattKeeper.models.GoalStage;
import lombok.Data;

import javax.validation.constraints.Future;
import java.time.LocalDate;
import java.util.List;

@Data
public class GoalDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate startDate;
    @Future
    private LocalDate deadline;
    private List<GoalStageDTO> stages;
    private int progress;

    public GoalDTO() {
    }

    public GoalDTO(Long id, String title, String description, LocalDate startDate, LocalDate deadline, List<GoalStageDTO> stages, int progress) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.deadline = deadline;
        this.stages = stages;
        this.progress=progress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public LocalDate getStartDate(LocalDate startDate){return startDate;}

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

}