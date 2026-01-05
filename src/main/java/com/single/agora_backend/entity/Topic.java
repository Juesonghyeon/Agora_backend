package com.single.agora_backend.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "topics")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String type;

    private String scale;
    private String difficulty;

    @Column(name = "participation_code")
    private String participationCode;

    @Column(nullable = false)
    private String status = "WAITING";

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private com.single.agora_backend.entity.User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getScale() { return scale; }
    public void setScale(String scale) { this.scale = scale; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getParticipationCode() { return participationCode; }
    public void setParticipationCode(String participationCode) { this.participationCode = participationCode; }
    public com.single.agora_backend.entity.User getUser() { return user; }
    public void setUser(com.single.agora_backend.entity.User user) { this.user = user; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
