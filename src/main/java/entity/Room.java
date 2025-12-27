package com.single.agora_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "room")
public class Room {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="topic_id", nullable=false)
    private Long topicId;

    @Column(name="participation_code", nullable=false, unique=true)
    private String participationCode;

    @Column(name="host_id")
    private Long hostId;

    @Column(name="status")
    private String status = "WAITING";

    @Column(name="current_phase")
    private String currentPhase;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="started_at")
    private LocalDateTime startedAt;

    @Column(name="ended_at")
    private LocalDateTime endedAt;

    // getters / setters
    // (생략 가능: Lombok 사용 시 @Data로 대체)
}
