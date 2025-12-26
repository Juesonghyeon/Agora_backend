package com.single.agora_backend.dto.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GameStateResponse {
    private String phase;
    private int timeLeft;
    private String topic;
    private List<String> team1Claims;
    private List<String> team2Claims;
}
