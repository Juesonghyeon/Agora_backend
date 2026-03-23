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

    // ⭐ 길이를 255로 확장하여 [object Object] 같은 비정상 문자열이 들어와도 DB 에러가 나지 않게 합니다.
    @Column(name="participation_code", nullable=false, unique=true, length=255)
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
}