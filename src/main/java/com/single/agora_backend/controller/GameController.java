package com.single.agora_backend.controller;

import com.single.agora_backend.dto.game.GameRequests;
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

    // 1. 코드 -> ID 조회 (입장 전)
    @GetMapping("/info/{code}")
    public Map<String, Long> getGameInfo(@PathVariable String code) {
        return Collections.singletonMap("id", gameService.findIdByCode(code));
    }

    // 2. 현재 상태 조회
    @GetMapping("/{lobbyId}")
    public void getState(@PathVariable Long lobbyId) {
        // 소켓 구독 시 자동으로 상태를 받지만, HTTP로 한 번 더 찌를 수 있음
    }

    // 3. 플레이어 입장 알림
    @PostMapping("/{lobbyId}/join")
    public void join(@PathVariable Long lobbyId, @RequestBody GameRequests.JoinRequest req) {
        gameService.joinPlayer(lobbyId, req);
    }

    // 4. 주제 제출 (Host) -> 의견 수렴 시작
    @PostMapping("/{lobbyId}/topic")
    public void submitTopic(@PathVariable Long lobbyId, @RequestBody Map<String, String> body) {
        gameService.startTopic(lobbyId, body.get("title"));
    }

    // 5. 개인 의견 제출 -> (전원 제출 시 자동 팀빌딩)
    @PostMapping("/{lobbyId}/opinion")
    public void submitOpinion(@PathVariable Long lobbyId, @RequestBody GameRequests.OpinionRequest req) {
        gameService.submitOpinion(lobbyId, req);
    }

    // 6. 팀장 투표
    @PostMapping("/{lobbyId}/vote")
    public void vote(@PathVariable Long lobbyId, @RequestBody GameRequests.VoteRequest req) {
        gameService.voteLeader(lobbyId, req);
    }

    // 7. 투표 종료 (타이머 끝)
    @PostMapping("/{lobbyId}/vote/end")
    public void endVote(@PathVariable Long lobbyId) {
        gameService.endVoting(lobbyId);
    }

    // 8. 팀장 발언 제출 (주장/근거/반론/변론 공용)
    @PostMapping("/{lobbyId}/action")
    public void submitAction(@PathVariable Long lobbyId, @RequestBody GameRequests.ActionRequest req) {
        gameService.submitTeamAction(lobbyId, req);
    }
}