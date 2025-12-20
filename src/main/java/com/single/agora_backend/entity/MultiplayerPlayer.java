package com.single.agora_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "multiplayer_players",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"game_code", "user_id"})
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MultiplayerPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_code", nullable = false)
    private String gameCode;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String role;
}
