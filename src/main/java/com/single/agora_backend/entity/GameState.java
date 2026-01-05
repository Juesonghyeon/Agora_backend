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
    private Long lobbyId;
    private int timeLeft;

    private String phase;
    private String topic;

    @ElementCollection
    private List<String> team1Claims = new ArrayList<>();

    @ElementCollection
    private List<String> team2Claims = new ArrayList<>();
}
