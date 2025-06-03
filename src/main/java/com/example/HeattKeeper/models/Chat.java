package com.example.HeattKeeper.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "chat",schema = "schema")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user1_id", referencedColumnName = "id")
    private User user1;

    @OneToOne
    @JoinColumn(name = "user2_id", referencedColumnName = "id")
    private User user2;
    @Column(nullable = false)
    @JoinColumn(name = "createdAt")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    public Chat() {}

    public Chat(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
    }
}
