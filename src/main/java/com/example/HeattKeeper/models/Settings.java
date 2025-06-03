package com.example.HeattKeeper.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "settings",schema = "schema")
public class Settings {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long userId;
    @Column(name="theme")
    private String theme;
    @Column(name="language")
    private String language;
}
