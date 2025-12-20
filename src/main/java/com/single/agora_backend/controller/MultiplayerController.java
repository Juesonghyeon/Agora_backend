package com.single.agora_backend.controller;

import com.single.agora_backend.entity.MultiplayerPlayer;
import com.single.agora_backend.entity.User;
import com.single.agora_backend.service.MultiplayerService;
import com.single.agora_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class MultiplayerController {

    private final MultiplayerService multiplayerService;
    private final UserService userService;

    // 참가
    @PostMapping("/join")
    public void joinGame(
            @RequestParam String gameCode,
            @RequestParam Long userId
    ) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        multiplayerService.joinGame(gameCode, user);
    }

    // 퇴장
    @PostMapping("/leave")
    public void leaveGame(
            @RequestParam String gameCode,
            @RequestParam Long userId
    ) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        multiplayerService.leaveGame(gameCode, user);
    }

    // 플레이어 목록
    @GetMapping("/players")
    public List<MultiplayerPlayer> getPlayers(@RequestParam String gameCode) {
        return multiplayerService.getPlayers(gameCode); // userId 포함
    }
}
