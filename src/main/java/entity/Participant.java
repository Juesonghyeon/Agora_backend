package com.single.agora_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "participant")
public class Participant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="room_id", nullable=false)
    private Long roomId;

    @Column(name="user_id")
    private Long userId;

    @Column(name="nickname")
    private String nickname;

    @Column(name="team")
    private String team;

    @Column(name="is_leader")
    private Boolean isLeader = false;

    @Column(name="connected")
    private Boolean connected = true;

    @Column(name="joined_at")
    private LocalDateTime joinedAt;

    @Column(name="left_at")
    private LocalDateTime leftAt;

    // getters / setters
}
