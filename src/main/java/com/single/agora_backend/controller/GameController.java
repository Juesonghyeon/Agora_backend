package com.single.agora_backend.controller;

import com.single.agora_backend.dto.game.ClaimRequest;
import com.single.agora_backend.dto.game.GameStateResponse;
import com.single.agora_backend.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping("/{lobbyId}")
    public GameStateResponse getState(@PathVariable Long lobbyId) {
        return gameService.getState(lobbyId);
    }

    @PostMapping("/{lobbyId}/topic")
    public boolean submitTopic(
            @PathVariable Long lobbyId,
            @RequestBody String topic
    ) {
        return gameService.submitTopic(lobbyId, topic);
    }

    @PostMapping("/{lobbyId}/claim")
    public void submitClaim(
            @PathVariable Long lobbyId,
            @RequestBody ClaimRequest req
    ) {
        gameService.submitClaim(lobbyId, req);
    }

    @PostMapping("/{lobbyId}/judge")
    public String judge(@PathVariable Long lobbyId) {
        return gameService.judge(lobbyId);
    }
}
