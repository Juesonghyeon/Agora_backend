package com.single.agora_backend.service;

import com.single.agora_backend.dto.game.ClaimRequest;
import com.single.agora_backend.dto.game.GameStateResponse;
import com.single.agora_backend.entity.GameState;
import com.single.agora_backend.repository.GameStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GameService {

    private final SimpMessagingTemplate template;
    private final GameStateRepository repository;
    private final ModeratorGptService moderatorService;
    private final JudgeGptService judgeService;

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
        // 1. AI 검증 수행
        if (!moderatorService.isValidTopic(topic)) {
            broadcast(lobbyId, "ERROR", "주제가 부적절합니다. (혐오, 단순 사실, 무의미 등)");
            return false;
        }

        GameState state = getOrCreate(lobbyId);
        state.setTopic(topic);
        state.setPhase("TEAM1_CLAIM"); // 팀1 차례로 변경
        state.setTimeLeft(60);         // 시간 60초 설정

        // 이전 기록 초기화 (새 주제니까)
        state.getTeam1Claims().clear();
        state.getTeam2Claims().clear();

        repository.save(state);
        broadcastState(lobbyId, state);
        return true;
    }


    // 발언 제출
    @Transactional
    public void submitClaim(Long lobbyId, ClaimRequest req) {
        GameState state = getOrCreate(lobbyId);

        // 주장 저장
        if ("team1".equals(req.getTeam())) {
            state.getTeam1Claims().add(req.getText());
            state.setPhase("TEAM2_CLAIM"); // 턴 넘김
            state.setTimeLeft(60);
        } else {
            state.getTeam2Claims().add(req.getText());
            // 팀2까지 끝났으므로 판정 혹은 검증 단계로
            checkLogicAndProceed(state);
        }

        repository.save(state);
        broadcastState(lobbyId, state);
    }

    /**
     * [수정됨] 컨트롤러에서 호출 가능한 수동 판정 메서드 (에러 해결)
     * Controller에서 gameService.judge(lobbyId)를 호출할 때 사용됩니다.
     */
    @Transactional
    public void judge(Long lobbyId) {
        GameState state = getOrCreate(lobbyId);
        String t1 = getLastClaim(state, true);
        String t2 = getLastClaim(state, false);

        // 주장이 없는 경우 방어 로직
        if (t1.isEmpty() || t2.isEmpty()) {
            broadcast(lobbyId, "ERROR", "판정을 진행하려면 양 팀의 주장이 필요합니다.");
            return;
        }

        processJudgment(state, t1, t2);
    }

    // 유사도 체크 및 판정 분기 (자동 흐름)
    private void checkLogicAndProceed(GameState state) {
        String t1 = getLastClaim(state, true);
        String t2 = getLastClaim(state, false);

        // 2. 유사도/엉뚱함 체크
        if (moderatorService.areClaimsTooSimilar(state.getTopic(), t1, t2)) {
            state.setPhase("TOPIC_SELECT"); // 다시 주제 선정으로
            state.setTopic(null);
            broadcast(state.getLobbyId(), "ERROR", "양팀 주장이 너무 비슷하거나 주제와 무관합니다. 주제를 다시 정해주세요.");
        } else {
            // 정상이면 판정 진행
            processJudgment(state, t1, t2);
        }
    }

    // 실제 판정 로직 (내부용)
    private void processJudgment(GameState state, String t1, String t2) {
        state.setPhase("JUDGEMENT");
        Map<String, Integer> scores = judgeService.scoreDebate(state.getTopic(), t1, t2);

        int s1 = scores.getOrDefault("team1", 0);
        int s2 = scores.getOrDefault("team2", 0);
        String winner = s1 > s2 ? "팀1 승리" : (s2 > s1 ? "팀2 승리" : "무승부");

        String resultMsg = String.format("결과: 팀1(%d점) vs 팀2(%d점) -> %s", s1, s2, winner);

        // 상태 저장 (DB 반영이 필요하다면 여기서 save)
        repository.save(state);

        broadcast(state.getLobbyId(), "RESULT", resultMsg);
        broadcastState(state.getLobbyId(), state); // 상태 업데이트 전송
    }

    // 헬퍼
    private GameState getOrCreate(Long lobbyId) {
        return repository.findById(lobbyId).orElseGet(() -> {
            GameState s = new GameState();
            s.setLobbyId(lobbyId);
            s.setPhase("TOPIC_SELECT");
            return repository.save(s);
        });
    }

    private String getLastClaim(GameState s, boolean isTeam1) {
        var list = isTeam1 ? s.getTeam1Claims() : s.getTeam2Claims();
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
                s.getTeam1Claims(), s.getTeam2Claims()
        );
    }
}