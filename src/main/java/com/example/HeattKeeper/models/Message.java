package com.example.HeattKeeper.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "message",schema = "schema")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    private String mediaUrl;
    private LocalDateTime timestamp;
    private boolean deletedForSender;
    private boolean deletedForReceiver;

    @ManyToOne
    private User sender;

    @ManyToOne
    private Chat chat;

    public Message() {
        this.timestamp = LocalDateTime.now();
        this.deletedForSender = false;
        this.deletedForReceiver = false;
    }

    public Message(String text, String mediaUrl, User sender, Chat chat) {
        this();
        this.text = text;
        this.mediaUrl = mediaUrl;
        this.sender = sender;
        this.chat = chat;
    }

    public boolean isVisibleFor(User user) {
        if (user.equals(sender)) {
            return !deletedForSender;
        } else {
            return !deletedForReceiver;
        }
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isDeletedForReceiver() {
        return deletedForReceiver;
    }

    public void setDeletedForReceiver(boolean deletedForReceiver) {
        this.deletedForReceiver = deletedForReceiver;
    }
}
