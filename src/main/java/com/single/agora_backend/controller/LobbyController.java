package com.single.agora_backend.controller;

import com.single.agora_backend.entity.LobbyPlayer;
import com.single.agora_backend.entity.User;
import com.single.agora_backend.service.LobbyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lobby")
public class LobbyController {

    private final LobbyService lobbyService;

    @PostMapping("/join")
    public void join(@AuthenticationPrincipal User user) {
        lobbyService.joinLobby(user);
    }

    @DeleteMapping("/leave")
    public void leave(@AuthenticationPrincipal User user) {
        lobbyService.leaveLobby(user);
    }

    @GetMapping("/players")
    public List<LobbyPlayer> players() {
        return lobbyService.getPlayers();
    }
}
