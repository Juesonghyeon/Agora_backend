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
    private String title; // 토픽 제목

    @Column(nullable = false)
    private String type; // "멀티" or "AI"

    @Column
    private String scale; // 멀티일 때: "소규모", "중규모", "대규모"

    @Column
    private String difficulty; // AI일 때: "쉬움", "보통", "어려움"

    @Column(name = "participation_code")
    private String participationCode;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    // Getter / Setter
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
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Timestamp getCreatedAt() { return createdAt; }
}
