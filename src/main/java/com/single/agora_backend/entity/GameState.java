package com.single.agora_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class GameState {

    @Id
    private Long lobbyId; // 숫자 ID (PK)

    // 🔥 [추가] 방 입장 코드 (FVCARD1IB3PK 등)
    // 이 필드가 있어야 코드로 ID를 찾을 수 있습니다.
    @Column(unique = true)
    private String participationCode;

    private int timeLeft;
    private String phase;
    private String topic;

    @ElementCollection
    private List<String> team1Claims = new ArrayList<>();

    @ElementCollection
    private List<String> team2Claims = new ArrayList<>();
}