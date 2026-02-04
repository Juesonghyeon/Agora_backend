package com.single.agora_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
public class GameState {

    @Id
    private Long lobbyId;

    @Column(unique = true)
    private String participationCode;

    private String topic;

    // 단계: TOPIC_SELECT -> GATHER_OPINIONS -> VOTE_LEADER -> ARGUMENT -> EVIDENCE -> REBUTTAL -> CLOSING -> JUDGEMENT
    private String phase;

    private int timeLeft;

    // 🔥 플레이어 관리 (SocketID -> 정보)
    @ElementCollection
    @CollectionTable(name = "game_players", joinColumns = @JoinColumn(name = "lobby_id"))
    @MapKeyColumn(name = "socket_id")
    private Map<String, PlayerInfo> players = new HashMap<>();

    // 팀장 ID (SocketID)
    private String team1Leader;
    private String team2Leader;

    // 단계별 발언 저장소 (주장, 근거, 반론, 변론 순서대로 쌓임)
    @ElementCollection
    private List<String> team1Claims = new ArrayList<>();

    @ElementCollection
    private List<String> team2Claims = new ArrayList<>();

    @Embeddable
    @Getter @Setter
    public static class PlayerInfo {
        private String nickname;
        private String initialOpinion; // 초기에 적은 의견 (팀 빌딩용)
        private String team;           // "team1" or "team2"
        private int voteCount;         // 팀장 투표 받은 수
    }
}