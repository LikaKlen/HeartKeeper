package com.example.HeattKeeper.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class MessageDTO {
    private Long id;
    private String sender;
    private String text;
    private String mediaUrl;
    private LocalDateTime timestamp;
    private boolean deletedForSender;
    public MessageDTO(Long id, String sender, String text, String mediaUrl, LocalDateTime timestamp, boolean deletedForSender) {
        this.id = id;
        this.sender = sender;
        this.text = text;
        this.mediaUrl = mediaUrl;
        this.timestamp = timestamp;
        this.deletedForSender = deletedForSender;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isDeletedForSender() {
        return deletedForSender;
    }

    public void setDeletedForSender(boolean deletedForSender) {
        this.deletedForSender = deletedForSender;
    }


}