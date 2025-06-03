package com.example.HeattKeeper.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "calendar_notes",schema = "schema")
public class CalendarNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="title")
    private String title;

    @Column(name="description",length = 1000)
    private String description;
    @Column(name="dateTime")
    private LocalDateTime dateTime;
    @Column(name="reminderEnabled")
    private boolean reminderEnabled;
    @Column(name="reminderRepeatDays")
    private int reminderRepeatDays; // 0 - нет повтора, >0 - повтор в днях
    @Column(name="location")
    private String location;
    @Column(name="locked")
    private boolean locked;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "partner_id")
    private User partner;

    @Enumerated(EnumType.STRING)
    private NoteColor lightThemeColor = NoteColor.DARK_PINK;

    @Enumerated(EnumType.STRING)
    private NoteColor darkThemeColor = NoteColor.WHITE;
}
