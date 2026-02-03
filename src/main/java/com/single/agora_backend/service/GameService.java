package com.single.agora_backend.service;

import com.single.agora_backend.dto.game.ClaimRequest;
import com.single.agora_backend.dto.game.GameStateResponse;
import com.single.agora_backend.entity.GameState;
import com.single.agora_backend.repository.GameStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GameService {

    private final SimpMessagingTemplate template;
    private final GameStateRepository repository;
    private final ModeratorGptService moderatorService;
    private final JudgeGptService judgeService;

    // ... findIdByCode, getState 등 기존 조회 메서드는 동일 ...
    @Transactional(readOnly = true)
    public Long findIdByCode(String code) {
        return repository.findByParticipationCode(code)
                .map(GameState::getLobbyId)
                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없습니다: " + code));
    }

    public GameStateResponse getState(Long lobbyId) {
        return toResponse(getOrCreate(lobbyId));
    }

    // 주제 제출
    @Transactional
    public boolean submitTopic(Long lobbyId, String topic) {
        if (!moderatorService.isValidTopic(topic)) {
            broadcast(lobbyId, "ERROR", "주제가 부적절합니다.");
            return false;
        }

        GameState state = getOrCreate(lobbyId);
        state.setTopic(topic);
        state.setPhase("TEAM1_CLAIM"); // 1라운드 시작
        state.setTimeLeft(60);

        if (state.getTeam1Claims() == null) state.setTeam1Claims(new ArrayList<>());
        if (state.getTeam2Claims() == null) state.setTeam2Claims(new ArrayList<>());
        state.getTeam1Claims().clear();
        state.getTeam2Claims().clear();

        repository.save(state);
        broadcastState(lobbyId, state);
        return true;
    }

    // 🔥 [핵심 수정] 발언 제출 및 4단계 흐름 제어
    @Transactional
    public void submitClaim(Long lobbyId, ClaimRequest req) {
        GameState state = getOrCreate(lobbyId);
        ensureLists(state);

        // 1. 발언 내용 검증 (쓸모없는 말이면 컷)
        if (!moderatorService.validateClaim(state.getTopic(), req.getText())) {
            broadcast(lobbyId, "ERROR", "발언이 주제와 무관하거나 내용이 부족합니다. 다시 입력해주세요.");
            return; // 저장 안 하고 리턴 (턴 유지)
        }

        // 2. 팀별 저장 및 페이즈 전환 로직
        String currentPhase = state.getPhase();

        if ("team1".equals(req.getTeam())) {
            state.getTeam1Claims().add(req.getText());

            if ("TEAM1_CLAIM".equals(currentPhase)) {
                state.setPhase("TEAM2_CLAIM"); // 1라운드: 팀1 -> 팀2
            } else if ("TEAM1_REBUTTAL".equals(currentPhase)) {
                state.setPhase("TEAM2_REBUTTAL"); // 2라운드: 팀1 -> 팀2
            }
            state.setTimeLeft(60);

        } else { // team2
            state.getTeam2Claims().add(req.getText());

            if ("TEAM2_CLAIM".equals(currentPhase)) {
                // 🔥 1라운드 종료 시점: 유사도 체크 수행
                if (checkSimilarityAndResetIfNeeded(state)) {
                    return; // 리셋되었으면 여기서 종료
                }
                state.setPhase("TEAM1_REBUTTAL"); // 문제 없으면 2라운드(반박) 시작
                state.setTimeLeft(60);

            } else if ("TEAM2_REBUTTAL".equals(currentPhase)) {
                // 🔥 2라운드 종료 시점: 판정 진행
                processJudgment(state);
                return; // 판정 함수 내부에서 저장/방송 하므로 리턴
            }
        }

        repository.save(state);
        broadcastState(lobbyId, state);
    }

    // 유사도 체크 로직
    private boolean checkSimilarityAndResetIfNeeded(GameState state) {
        String t1 = getLastClaim(state, true);
        String t2 = getLastClaim(state, false);

        if (moderatorService.areClaimsTooSimilar(state.getTopic(), t1, t2)) {
            state.setPhase("TOPIC_SELECT"); // 주제 선정으로 롤백
            state.setTopic(null);
            repository.save(state);
            broadcast(state.getLobbyId(), "ERROR", "양팀의 주장이 너무 비슷하여 토론이 성립되지 않습니다. 주제를 다시 정해주세요.");
            broadcastState(state.getLobbyId(), state);
            return true; // 리셋됨
        }
        return false; // 통과
    }

    // 수동 판정 (Controller용)
    @Transactional
    public void judge(Long lobbyId) {
        processJudgment(getOrCreate(lobbyId));
    }

    private void processJudgment(GameState state) {
        String t1Claims = String.join(" -> ", state.getTeam1Claims());
        String t2Claims = String.join(" -> ", state.getTeam2Claims());

        state.setPhase("JUDGEMENT");
        broadcastState(state.getLobbyId(), state); // 판정 중 표시

        Map<String, Integer> scores = judgeService.scoreDebate(state.getTopic(), t1Claims, t2Claims);

        int s1 = scores.getOrDefault("team1", 0);
        int s2 = scores.getOrDefault("team2", 0);
        String winner = s1 > s2 ? "팀1 승리" : (s2 > s1 ? "팀2 승리" : "무승부");
        String resultMsg = String.format("결과: 팀1(%d점) vs 팀2(%d점) -> %s", s1, s2, winner);

        repository.save(state);
        broadcast(state.getLobbyId(), "RESULT", resultMsg);
    }

    // 헬퍼
    private GameState getOrCreate(Long lobbyId) {
        return repository.findById(lobbyId).orElseGet(() -> {
            GameState s = new GameState();
            s.setLobbyId(lobbyId);
            s.setPhase("TOPIC_SELECT");
            s.setTeam1Claims(new ArrayList<>());
            s.setTeam2Claims(new ArrayList<>());
            return repository.save(s);
        });
    }

    private void ensureLists(GameState s) {
        if (s.getTeam1Claims() == null) s.setTeam1Claims(new ArrayList<>());
        if (s.getTeam2Claims() == null) s.setTeam2Claims(new ArrayList<>());
    }

    private String getLastClaim(GameState s, boolean isTeam1) {
        List<String> list = isTeam1 ? s.getTeam1Claims() : s.getTeam2Claims();
        return (list == null || list.isEmpty()) ? "" : list.get(list.size() - 1);
    }

    private void broadcastState(Long lobbyId, GameState state) {
        template.convertAndSend("/topic/game/" + lobbyId, toResponse(state));
    }

    private void broadcast(Long lobbyId, String type, String msg) {
        template.convertAndSend("/topic/game/" + lobbyId, Map.of("type", type, "message", msg));
    }

    private GameStateResponse toResponse(GameState s) {
        return new GameStateResponse(
                s.getPhase(), s.getTimeLeft(), s.getTopic(),
                s.getTeam1Claims() != null ? s.getTeam1Claims() : new ArrayList<>(),
                s.getTeam2Claims() != null ? s.getTeam2Claims() : new ArrayList<>()
        );
    }
}