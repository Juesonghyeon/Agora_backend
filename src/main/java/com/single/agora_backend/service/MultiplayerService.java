package com.single.agora_backend.service;

import com.single.agora_backend.entity.MultiplayerPlayer;
import com.single.agora_backend.entity.Topic;
import com.single.agora_backend.entity.User;
import com.single.agora_backend.repository.MultiplayerPlayerRepository;
import com.single.agora_backend.repository.TopicRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MultiplayerService {

    private final MultiplayerPlayerRepository repository;
    private final TopicRepository topicRepository;
    private final com.single.agora_backend.service.SocketClient socketClient;

    // ✅ 게임 코드 생성
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

    // ✅ 게임 참가 (HOST / PLAYER 자동 판별)
    @Transactional
    public void joinGame(String gameCode, User user) {
        if (repository.existsByGameCodeAndUserId(gameCode, user.getId())) return;

        Topic topic = topicRepository
                .findByParticipationCode(gameCode)
                .orElseThrow(() -> new RuntimeException("방 없음"));

        String role = topic.getUser().getId().equals(user.getId())
                ? "HOST"
                : "PLAYER";

        repository.save(
                new MultiplayerPlayer(
                        null,
                        gameCode,
                        user.getId(),
                        user.getUsername(),
                        role
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

    // ✅ 게임 시작 (HOST만 가능)
    @Transactional
    public void startGame(String gameCode, Long userId) {
        Topic topic = topicRepository
                .findByParticipationCode(gameCode)
                .orElseThrow(() -> new RuntimeException("방 없음"));

        if (!topic.getUser().getId().equals(userId)) {
            throw new RuntimeException("HOST만 게임 시작 가능");
        }

        topic.setStatus("PLAYING");
        topicRepository.save(topic);

        socketClient.sendGameStart(gameCode);
    }

    // ✅ 게임 종료 정리
    @Transactional
    public void closeGame(String gameCode) {
        repository.deleteAllByGameCode(gameCode);
    }
}
