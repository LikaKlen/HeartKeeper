package com.example.HeattKeeper.models;

import com.example.HeattKeeper.dto.GoalStageDTO;
import jakarta.persistence.*;
import lombok.Data;

import javax.validation.constraints.Future;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commongoal", schema = "schema")
public class CommonGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "startDate")
    private LocalDate startDate;

    @Column(name = "deadline")
    @Future
    private LocalDate deadline;

    @Column(name = "progress")
    private int progress; // 0-100, updated via helper method

    @ManyToOne
    private User owner;

    @ManyToOne
    private User partner;

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GoalStage> stages = new ArrayList<>();

    // Auto-update progress from stages
    public void updateProgress() {
        if (stages == null || stages.isEmpty()) {
            this.progress = 0;
            return;
        }

        long completed = stages.stream().filter(GoalStage::isCompleted).count();
        this.progress = (int) ((double) completed / stages.size() * 100);
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public User getPartner() { return partner; }
    public void setPartner(User partner) { this.partner = partner; }

    public List<GoalStage> getStages() { return stages; }
    public void setStages(List<GoalStage> stages) { this.stages = stages; }
}