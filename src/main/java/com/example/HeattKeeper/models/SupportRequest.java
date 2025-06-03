package com.example.HeattKeeper.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "supportRequest",schema = "schema")
public class SupportRequest {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User userId;
    @Column(name="email")
    private String email;
    @Column(name="message")
    private String message;
}
