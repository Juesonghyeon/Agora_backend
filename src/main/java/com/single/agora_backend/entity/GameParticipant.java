package com.single.agora_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "game_participants",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"game_code", "user_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_code", nullable = false, length = 255)
    private String gameCode;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String role; // HOST, PARTICIPANT 등

    // ⭐ 핵심: 이 필드가 있어야 사이드바에 사진이 나옵니다.
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt = LocalDateTime.now();
}