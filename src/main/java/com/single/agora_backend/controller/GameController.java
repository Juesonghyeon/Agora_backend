package com.single.agora_backend.controller;

import com.single.agora_backend.dto.TopicRequest;
import com.single.agora_backend.dto.game.ClaimRequest;
import com.single.agora_backend.dto.game.GameStateResponse;
import com.single.agora_backend.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    // 🔥 [추가] 방 코드(String) -> 방 ID(Long) 변환 API
    // 프론트의 fetchGameInfo 함수가 이 API를 호출합니다.
    @GetMapping("/info/{code}")
    public Map<String, Long> getGameIdByCode(@PathVariable String code) {
        Long gameId = gameService.findIdByCode(code);
        return Collections.singletonMap("id", gameId);
    }

    @GetMapping("/{lobbyId}")
    public GameStateResponse getState(@PathVariable Long lobbyId) {
        return gameService.getState(lobbyId);
    }

    @PostMapping("/{lobbyId}/topic")
    public boolean submitTopic(
            @PathVariable Long lobbyId,
            @RequestBody TopicRequest req
    ) {
        return gameService.submitTopic(lobbyId, req.getTitle());
    }

    @PostMapping("/{lobbyId}/claim")
    public void submitClaim(
            @PathVariable Long lobbyId,
            @RequestBody ClaimRequest req
    ) {
        gameService.submitClaim(lobbyId, req);
    }

    // 👇 [수정된 부분] void 메서드를 호출하고, 성공 메시지를 반환하도록 변경
    @PostMapping("/{lobbyId}/judge")
    public String judge(@PathVariable Long lobbyId) {
        gameService.judge(lobbyId); // void 메서드 실행
        return "JUDGEMENT_STARTED"; // 클라이언트에 성공 메시지 반환
    }
}