package com.single.agora_backend.dto.game;

import lombok.Getter;

@Getter
public class ClaimRequest {
    private String team;   // team1 / team2
    private String text;
}
