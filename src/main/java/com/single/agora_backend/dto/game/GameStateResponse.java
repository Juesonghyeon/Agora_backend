package com.single.agora_backend.dto.game;

import com.single.agora_backend.entity.GameState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class GameStateResponse {
    private Long lobbyId;
    private String phase;
    private int timeLeft;
    private String topic;

    // 현재 팀 상황
    private Map<String, GameState.PlayerInfo> players;
    private String team1Leader;
    private String team2Leader;

    // 현재까지의 발언 목록
    private List<String> team1Claims;
    private List<String> team2Claims;
}