package com.single.agora_backend.service;

import com.single.agora_backend.entity.MultiplayerPlayer;
import com.single.agora_backend.entity.User;
import com.single.agora_backend.repository.MultiplayerPlayerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MultiplayerService {

    private final MultiplayerPlayerRepository repository;

    // ✅ 게임 생성
    public String createGame() {
        String gameCode;

        do {
            gameCode = UUID.randomUUID()
                    .toString()
                    .substring(0, 6)
                    .toUpperCase();
        } while (repository.existsByGameCode(gameCode));

        return gameCode;
    }

    // ✅ 게임 참가
    @Transactional
    public void joinGame(String gameCode, User user) {
        if (repository.existsByGameCodeAndUserId(gameCode, user.getId())) return;

        repository.save(
                new MultiplayerPlayer(
                        null,
                        gameCode,
                        user.getId(),
                        user.getUsername(),
                        "PLAYER"
                )
        );
    }

    // ✅ 게임 퇴장
    @Transactional
    public void leaveGame(String gameCode, User user) {
        repository.deleteByGameCodeAndUserId(gameCode, user.getId());
    }

    // ✅ 플레이어 목록
    public List<MultiplayerPlayer> getPlayers(String gameCode) {
        return repository.findByGameCode(gameCode);
    }

    // ✅ 게임 종료 시 정리
    @Transactional
    public void closeGame(String gameCode) {
        repository.deleteAllByGameCode(gameCode);
    }

}
