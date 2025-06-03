package com.example.HeattKeeper.dto;

import lombok.Data;

@Data
public class SendMessageRequest {
    private Long chatId;
    private Long senderId;
    private String content;
}