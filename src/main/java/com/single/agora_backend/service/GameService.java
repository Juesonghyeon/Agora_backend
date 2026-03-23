package com.single.agora_backend.service;

import com.single.agora_backend.dto.game.GameRequests;
import com.single.agora_backend.dto.game.GameStateResponse;
import com.single.agora_backend.dto.gpt.GptRequest;
import com.single.agora_backend.entity.GameState;
import com.single.agora_backend.entity.MultiplayerPlayer;
import com.single.agora_backend.entity.Topic;
import com.single.agora_backend.repository.GameStateRepository;
import com.single.agora_backend.repository.MultiplayerPlayerRepository;
import com.single.agora_backend.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {

    private final GameStateRepository repository;
    private final SimpMessagingTemplate template;
    private final MultiplayerPlayerRepository multiplayerPlayerRepository;
    private final TopicRepository topicRepository;

    // backend.txt의 실제 서비스명으로 교체
    private final TeamBuildingGptService teamBuildingService;
    private final JudgeGptService judgeGptService; // judgeService 대체
    private final ModeratorGptService moderatorService;

    @Transactional(readOnly = true)
    public Long findIdByCode(String code) {
        return topicRepository.findByParticipationCode(code)
                .map(Topic::getId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게임 코드입니다."));
    }

    @Transactional
    public void joinPlayer(Long lobbyId, GameRequests.JoinRequest req) {
        GameState state = getOrCreate(lobbyId);
        Topic topic = topicRepository.findById(lobbyId)
                .orElseThrow(() -> new RuntimeException("방을 찾을 수 없습니다."));
        String gameCode = topic.getParticipationCode();

        // DB 유저 정보 확인 (Host 여부 판별용)
        Optional<MultiplayerPlayer> dbPlayer = multiplayerPlayerRepository.findByGameCode(gameCode)
                .stream()
                .filter(p -> p.getUsername().trim().equalsIgnoreCase(req.getNickname().trim()))
                .findFirst();

        GameState.PlayerInfo info = new GameState.PlayerInfo();
        info.setNickname(req.getNickname());

        // HOST 설정 로직 강화
        if (dbPlayer.isPresent() && "HOST".equalsIgnoreCase(dbPlayer.get().getRole())) {
            info.setHost(true);
        } else if (state.getPlayers().isEmpty()) {
            info.setHost(true);
        } else {
            info.setHost(false);
        }

        state.getPlayers().put(req.getSocketId(), info);
        repository.save(state);
        broadcastState(lobbyId, state);
    }

    @Transactional
    public void startTopic(Long lobbyId, String topic) {
        if (!moderatorService.isValidTopic(topic)) {
            broadcast(lobbyId, "ERROR", "주제가 부적절하거나 무의미합니다. 다시 입력해주세요.");
            return;
        }

        GameState state = getOrCreate(lobbyId);
        state.setTopic(topic);
        state.setPhase("GATHER_OPINIONS");
        state.setTimeLeft(120);

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

    @Transactional
    public void submitOpinion(Long lobbyId, GameRequests.OpinionRequest req) {
        GameState state = getOrCreate(lobbyId);
        GameState.PlayerInfo p = state.getPlayers().get(req.getSocketId());

        if (p != null) {
            p.setInitialOpinion(req.getOpinion());
        }
        repository.save(state);

        // 모든 인원이 의견 제출했는지 확인
        boolean allSubmitted = state.getPlayers().values().stream()
                .allMatch(info -> info.getInitialOpinion() != null && !info.getInitialOpinion().isEmpty());

        if (allSubmitted && state.getPlayers().size() >= 2) {
            processTeamBuilding(state);
        } else {
            broadcastState(lobbyId, state);
        }
    }

    private void processTeamBuilding(GameState state) {
        Map<String, List<String>> teams = teamBuildingService.clusterPlayers(state.getTopic(), state.getPlayers());

        if (teams == null || teams.get("team1").isEmpty() || teams.get("team2").isEmpty()) {
            state.setPhase("TOPIC_SELECT");
            state.setTopic(null);
            broadcast(state.getLobbyId(), "ERROR", "의견이 너무 한쪽으로 치우쳐 팀을 나눌 수 없습니다. 새로운 주제를 입력해주세요.");
        } else {
            teams.get("team1").forEach(id -> state.getPlayers().get(id).setTeam("team1"));
            teams.get("team2").forEach(id -> state.getPlayers().get(id).setTeam("team2"));
            state.setPhase("VOTE_LEADER");
            state.setTimeLeft(60);
        }
        repository.save(state);
        broadcastState(state.getLobbyId(), state);
    }

    @Transactional
    public void voteLeader(Long lobbyId, GameRequests.VoteRequest req) {
        GameState state = getOrCreate(lobbyId);
        // candidateId는 SocketId여야 함
        GameState.PlayerInfo candidate = state.getPlayers().get(req.getCandidateId());

        if (candidate != null) {
            candidate.setVoteCount(candidate.getVoteCount() + 1);
        }

        int totalVotes = state.getPlayers().values().stream().mapToInt(GameState.PlayerInfo::getVoteCount).sum();

        if (totalVotes >= state.getPlayers().size()) {
            endVoting(lobbyId);
        } else {
            repository.save(state);
            broadcastState(lobbyId, state);
        }
    }

    @Transactional
    public void endVoting(Long lobbyId) {
        GameState state = getOrCreate(lobbyId);
        state.setTeam1Leader(pickLeader(state, "team1"));
        state.setTeam2Leader(pickLeader(state, "team2"));
        state.setPhase("ARGUMENT"); // 리더 확정 후 입론 단계로 바로 이동
        state.setTimeLeft(180);
        repository.save(state);
        broadcastState(lobbyId, state);
    }

    private String pickLeader(GameState state, String teamName) {
        return state.getPlayers().entrySet().stream()
                .filter(e -> teamName.equals(e.getValue().getTeam()))
                .max(Comparator.comparingInt(e -> e.getValue().getVoteCount()))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    @Transactional
    public void submitTeamAction(Long lobbyId, GameRequests.ActionRequest req) {
        GameState state = getOrCreate(lobbyId);

        // 요청자가 리더인지 확인
        boolean isTeam1Leader = req.getLeaderId().equals(state.getTeam1Leader());
        boolean isTeam2Leader = req.getLeaderId().equals(state.getTeam2Leader());

        if (!isTeam1Leader && !isTeam2Leader) return;

        int blueCount = state.getTeam1Claims().size();
        int redCount = state.getTeam2Claims().size();

        boolean isBlueTurn = false;
        boolean isRedTurn = false;

        // 턴 판별 로직 (제공해주신 규칙 기반)
        if (blueCount == 0 && redCount == 0) isRedTurn = true;           // 입론-Red 선공
        else if (blueCount == 0 && redCount == 1) isBlueTurn = true;     // 입론-Blue
        else if (blueCount == 1 && redCount == 1) isRedTurn = true;      // 근거-Red 선공
        else if (blueCount == 1 && redCount == 2) isBlueTurn = true;     // 근거-Blue
        else if (blueCount == 2 && redCount == 2) isBlueTurn = true;     // 반론-Blue 선공
        else if (blueCount == 3 && redCount == 2) isRedTurn = true;      // 반론-Red
        else if (blueCount == 3 && redCount == 3) isBlueTurn = true;     // 변론-Blue 선공
        else if (blueCount == 4 && redCount == 3) isRedTurn = true;      // 변론-Red

        // 잘못된 턴에 요청 시 무시
        if (isTeam1Leader && !isBlueTurn) return;
        if (isTeam2Leader && !isRedTurn) return;

        // 발언 저장 (Team 1 = Blue, Team 2 = Red)
        if (isTeam1Leader) state.getTeam1Claims().add(req.getContent());
        else state.getTeam2Claims().add(req.getContent());

        // 양팀 발언 횟수가 같아지면 다음 페이즈로 이동 (입론/근거/반론/변론 완료 시)
        int currentMaxCount = Math.max(state.getTeam1Claims().size(), state.getTeam2Claims().size());
        int currentMinCount = Math.min(state.getTeam1Claims().size(), state.getTeam2Claims().size());

        if (currentMaxCount == currentMinCount) {
            advancePhase(state);
        } else {
            repository.save(state);
            broadcastState(lobbyId, state);
        }
    }

    private void advancePhase(GameState state) {
        int turnCount = state.getTeam1Claims().size(); // 양팀 횟수가 같으므로 한쪽만 체크

        switch (turnCount) {
            case 1 -> state.setPhase("EVIDENCE");
            case 2 -> state.setPhase("REBUTTAL");
            case 3 -> state.setPhase("CLOSING");
            case 4 -> {
                processJudgment(state);
                return;
            }
            default -> { /* 예외 상황 */ }
        }
        state.setTimeLeft(180);
        repository.save(state);
        broadcastState(state.getLobbyId(), state);
    }

    private void processJudgment(GameState state) {
        state.setPhase("JUDGEMENT");
        broadcastState(state.getLobbyId(), state);

        // 1. AI 판정을 위해 팀별 발언 기록을 줄바꿈으로 구분된 하나의 문자열로 합칩니다.
        // (GPT가 문맥을 파악하기 가장 좋은 형태입니다)
        String t1History = String.join("\n", state.getTeam1Claims());
        String t2History = String.join("\n", state.getTeam2Claims());

        try {
            // 2. JudgeGptService의 시그니처에 맞춰 인자 3개(주제, 팀1기록, 팀2기록)만 전달합니다.
            // 서비스 내부에서 프롬프트 생성 및 GPT 호출을 모두 처리합니다.
            String resultMsg = judgeGptService.judgeDebate(
                    state.getTopic(),
                    t1History,
                    t2History
            );

            broadcast(state.getLobbyId(), "RESULT", resultMsg);
        } catch(Exception e) {
            log.error("Judgment failed", e);
            broadcast(state.getLobbyId(), "RESULT", "## ⚠️ AI 심사 중 오류가 발생했습니다.\n잠시 후 다시 시도하거나 관리자에게 문의해주세요.");
        }

        // 게임 데이터 정리
        repository.delete(state);
    }
    private GameState getOrCreate(Long lobbyId) {
        return repository.findById(lobbyId).orElseGet(() -> {
            GameState s = new GameState();
            s.setLobbyId(lobbyId);
            s.setPhase("TOPIC_SELECT");
            s.setPlayers(new HashMap<>());
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