package com.example.HeattKeeper.dto;

import lombok.Data;

@Data
public class AIRecommendationRequest {
    private String type;

    public AIRecommendationRequest(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}