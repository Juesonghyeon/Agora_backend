package com.single.agora_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "lobby_player")
@Getter
@NoArgsConstructor
public class LobbyPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String gameCode;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String username;

    private String profileImageUrl; // 사진 경로 필드

    private LocalDateTime joinedAt = LocalDateTime.now();

    // 생성자 수정: 사진 경로까지 포함
    public LobbyPlayer(String gameCode, Long userId, String username, String profileImageUrl) {
        this.gameCode = gameCode;
        this.userId = userId;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
    }
}