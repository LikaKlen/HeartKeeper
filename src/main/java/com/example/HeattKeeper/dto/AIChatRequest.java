package com.example.HeattKeeper.dto;


import lombok.Data;


@Data
public class AIChatRequest {
    private String message;
    private String context;

    public AIChatRequest(String message, String context) {
        this.message = message;
        this.context = context;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}