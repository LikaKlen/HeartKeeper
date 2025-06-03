package com.example.HeattKeeper.dto;

import lombok.Data;

@Data
public class WeeklyReport {
    private String challenge;
    private String dateIdea;
    private String tip;

    // Getters and setters
    public String getChallenge() { return challenge; }
    public void setChallenge(String challenge) { this.challenge = challenge; }
    public String getDateIdea() { return dateIdea; }
    public void setDateIdea(String dateIdea) { this.dateIdea = dateIdea; }
    public String getTip() { return tip; }
    public void setTip(String tip) { this.tip = tip; }
}