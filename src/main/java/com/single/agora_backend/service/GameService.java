package com.single.agora_backend.service;

import com.single.agora_backend.dto.game.GameRequests;
import com.single.agora_backend.dto.game.GameStateResponse;
import com.single.agora_backend.entity.GameState;
import com.single.agora_backend.repository.GameStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameStateRepository repository;
    private final SimpMessagingTemplate template;
    private final TeamBuildingGptService teamBuildingService;
    private final JudgeGptService judgeService; // 기존 판정 서비스 사용

    // 1. 플레이어 입장
    @Transactional
    public void joinPlayer(Long lobbyId, GameRequests.JoinRequest req) {
        GameState state = getOrCreate(lobbyId);

        GameState.PlayerInfo info = new GameState.PlayerInfo();
        info.setNickname(req.getNickname());
        state.getPlayers().put(req.getSocketId(), info);

        repository.save(state);
        broadcastState(lobbyId, state);
    }

    // 2. 주제 선정 (게임 시작)
    @Transactional
    public void startTopic(Long lobbyId, String topic) {
        GameState state = getOrCreate(lobbyId);
        state.setTopic(topic);
        state.setPhase("GATHER_OPINIONS"); // 의견 수렴 단계 시작
        state.setTimeLeft(120);

        // 초기화
        state.setTeam1Claims(new ArrayList<>());
        state.setTeam2Claims(new ArrayList<>());
        state.getPlayers().values().forEach(p -> {
            p.setInitialOpinion(null);
            p.setTeam(null);
            p.setVoteCount(0);
        });

        repository.save(state);
        broadcastState(lobbyId, state);
    }

    // 3. 의견 제출 및 자동 팀 빌딩
    @Transactional
    public void submitOpinion(Long lobbyId, GameRequests.OpinionRequest req) {
        GameState state = getOrCreate(lobbyId);
        GameState.PlayerInfo p = state.getPlayers().get(req.getSocketId());
        if (p != null) p.setInitialOpinion(req.getOpinion());

        repository.save(state);

        // 모두 제출했는지 확인
        boolean allSubmitted = state.getPlayers().values().stream()
                .allMatch(info -> info.getInitialOpinion() != null && !info.getInitialOpinion().isEmpty());

        if (allSubmitted && state.getPlayers().size() >= 2) { // 최소 2명
            processTeamBuilding(state);
        } else {
            broadcastState(lobbyId, state);
        }
    }

    private void processTeamBuilding(GameState state) {
        broadcast(state.getLobbyId(), "INFO", "AI가 팀을 편성 중입니다...");

        Map<String, List<String>> teams = teamBuildingService.clusterPlayers(state.getTopic(), state.getPlayers());

        if (teams == null) {
            // 실패: 주제 재선정으로 롤백
            state.setPhase("TOPIC_SELECT");
            state.setTopic(null);
            broadcast(state.getLobbyId(), "ERROR", "의견이 너무 갈리거나 모호하여 팀을 나눌 수 없습니다. 주제를 다시 정해주세요.");
        } else {
            // 성공: 팀 배정 적용
            teams.get("team1").forEach(id -> state.getPlayers().get(id).setTeam("team1"));
            teams.get("team2").forEach(id -> state.getPlayers().get(id).setTeam("team2"));

            state.setPhase("VOTE_LEADER");
            state.setTimeLeft(60);
        }
        repository.save(state);
        broadcastState(state.getLobbyId(), state);
    }

    // 4. 팀장 투표
    @Transactional
    public void voteLeader(Long lobbyId, GameRequests.VoteRequest req) {
        GameState state = getOrCreate(lobbyId);
        GameState.PlayerInfo candidate = state.getPlayers().get(req.getCandidateId());

        if (candidate != null) {
            candidate.setVoteCount(candidate.getVoteCount() + 1);
        }
        repository.save(state);
        broadcastState(lobbyId, state);
    }

    // 투표 종료 (타이머 끝 or 강제 종료)
    @Transactional
    public void endVoting(Long lobbyId) {
        GameState state = getOrCreate(lobbyId);

        // 팀별 최다 득표자 선정
        state.setTeam1Leader(pickLeader(state, "team1"));
        state.setTeam2Leader(pickLeader(state, "team2"));

        state.setPhase("ARGUMENT"); // 첫 번째 토론 단계: 주장
        state.setTimeLeft(180); // 논의 시간 포함

        repository.save(state);
        broadcastState(lobbyId, state);
    }

    private String pickLeader(GameState state, String teamName) {
        return state.getPlayers().entrySet().stream()
                .filter(e -> teamName.equals(e.getValue().getTeam()))
                .max(Comparator.comparingInt(e -> e.getValue().getVoteCount()))
                .map(Map.Entry::getKey)
                .orElse(null); // 없으면 null (로직상 없을 수 없음)
    }

    // 5. 단계별 발언 제출 (주장 -> 근거 -> 반론 -> 변론)
    @Transactional
    public void submitTeamAction(Long lobbyId, GameRequests.ActionRequest req) {
        GameState state = getOrCreate(lobbyId);

        // 팀장 검증
        boolean isTeam1 = req.getLeaderId().equals(state.getTeam1Leader());
        boolean isTeam2 = req.getLeaderId().equals(state.getTeam2Leader());

        if (!isTeam1 && !isTeam2) return; // 팀장 아님

        List<String> currentList = isTeam1 ? state.getTeam1Claims() : state.getTeam2Claims();

        // 현재 단계에 맞는 인덱스인지 확인 (중복 제출 방지)
        int currentTurnIndex = getTurnIndex(state.getPhase());
        if (currentList.size() > currentTurnIndex) return; // 이미 제출함

        currentList.add(req.getContent());

        // 양 팀 모두 제출했으면 다음 단계로
        if (state.getTeam1Claims().size() == state.getTeam2Claims().size()) {
            advancePhase(state);
        } else {
            repository.save(state);
            broadcastState(lobbyId, state); // 대기 상태 전송
        }
    }

    private int getTurnIndex(String phase) {
        return switch (phase) {
            case "ARGUMENT" -> 0;
            case "EVIDENCE" -> 1;
            case "REBUTTAL" -> 2;
            case "CLOSING" -> 3;
            default -> -1;
        };
    }

    private void advancePhase(GameState state) {
        switch (state.getPhase()) {
            case "ARGUMENT" -> { state.setPhase("EVIDENCE"); state.setTimeLeft(180); }
            case "EVIDENCE" -> { state.setPhase("REBUTTAL"); state.setTimeLeft(180); }
            case "REBUTTAL" -> { state.setPhase("CLOSING"); state.setTimeLeft(180); }
            case "CLOSING" -> {
                processJudgment(state); // 판정 시작
                return;
            }
        }
        repository.save(state);
        broadcastState(state.getLobbyId(), state);
    }

    // 6. 판정 (기존 로직 활용)
    private void processJudgment(GameState state) {
        state.setPhase("JUDGEMENT");
        broadcastState(state.getLobbyId(), state);

        String t1Full = String.join(" -> ", state.getTeam1Claims());
        String t2Full = String.join(" -> ", state.getTeam2Claims());

        Map<String, Integer> scores = judgeService.scoreDebate(state.getTopic(), t1Full, t2Full);
        int s1 = scores.getOrDefault("team1", 50);
        int s2 = scores.getOrDefault("team2", 50);

        String resultMsg = String.format("결과: 팀1 %d점 vs 팀2 %d점", s1, s2);
        broadcast(state.getLobbyId(), "RESULT", resultMsg);

        repository.save(state);
    }

    // --- 유틸 ---
    @Transactional(readOnly = true)
    public Long findIdByCode(String code) {
        return repository.findByParticipationCode(code)
                .map(GameState::getLobbyId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Code"));
    }

    private GameState getOrCreate(Long lobbyId) {
        return repository.findById(lobbyId).orElseGet(() -> {
            GameState s = new GameState();
            s.setLobbyId(lobbyId);
            s.setPhase("TOPIC_SELECT");
            return repository.save(s);
        });
    }

    private void broadcastState(Long lobbyId, GameState s) {
        GameStateResponse res = GameStateResponse.builder()
                .lobbyId(s.getLobbyId())
                .phase(s.getPhase())
                .timeLeft(s.getTimeLeft())
                .topic(s.getTopic())
                .players(s.getPlayers())
                .team1Leader(s.getTeam1Leader())
                .team2Leader(s.getTeam2Leader())
                .team1Claims(s.getTeam1Claims())
                .team2Claims(s.getTeam2Claims())
                .build();
        template.convertAndSend("/topic/game/" + lobbyId, res);
    }

    private void broadcast(Long lobbyId, String type, String msg) {
        template.convertAndSend("/topic/game/" + lobbyId, Map.of("type", type, "message", msg));
    }
}