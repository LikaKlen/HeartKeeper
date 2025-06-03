package com.example.HeattKeeper.dto;

import lombok.Data;

@Data
public class MessageRequest {
    private String text;
    private String target; // "AI" or "partner"

    public MessageRequest(String text, String target) {
        this.text = text;
        this.target = target;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
// getters and setters
}