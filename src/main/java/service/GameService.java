package com.single.agora_backend.service;

import com.single.agora_backend.dto.game.ClaimRequest;
import com.single.agora_backend.dto.game.GameStateResponse;
import com.single.agora_backend.entity.GameState;
import com.single.agora_backend.repository.GameStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameStateRepository repository;
    private final ModeratorGptService moderatorGptService;
    private final JudgeGptService judgeGptService;

    public GameStateResponse getState(Long lobbyId) {
        GameState state = getOrCreate(lobbyId);
        return toResponse(state);
    }

    public boolean submitTopic(Long lobbyId, String topic) {
        if (!moderatorGptService.isValidTopic(topic)) return false;

        GameState state = getOrCreate(lobbyId);
        state.setTopic(topic);
        state.setPhase("NEXT");
        repository.save(state);
        return true;
    }

    public void submitClaim(Long lobbyId, ClaimRequest req) {
        GameState state = getOrCreate(lobbyId);

        if ("team1".equals(req.getTeam())) {
            state.getTeam1Claims().add(req.getText());
        } else {
            state.getTeam2Claims().add(req.getText());
        }

        repository.save(state);
    }

    public String judge(Long lobbyId) {
        GameState state = getOrCreate(lobbyId);
        return judgeGptService.judge(
                state.getTopic(),
                state.getTeam1Claims(),
                state.getTeam2Claims()
        );
    }

    private GameState getOrCreate(Long lobbyId) {
        return repository.findById(lobbyId).orElseGet(() -> {
            GameState s = new GameState();
            s.setLobbyId(lobbyId);
            s.setPhase("TOPIC_SELECT");
            return repository.save(s);
        });
    }

    private GameStateResponse toResponse(GameState s) {
        return new GameStateResponse(
                s.getPhase(),
                s.getTimeLeft(),   // 🔥 이 줄 추가
                s.getTopic(),
                s.getTeam1Claims(),
                s.getTeam2Claims()
        );
    }
}
