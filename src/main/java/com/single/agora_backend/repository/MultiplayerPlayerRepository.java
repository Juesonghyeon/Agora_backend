package com.single.agora_backend.repository;

import com.single.agora_backend.entity.MultiplayerPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MultiplayerPlayerRepository
        extends JpaRepository<MultiplayerPlayer, Long> {

    List<MultiplayerPlayer> findByGameCode(String gameCode);

    boolean existsByGameCode(String gameCode);

    boolean existsByGameCodeAndUserId(String gameCode, Long userId);

    void deleteByGameCodeAndUserId(String gameCode, Long userId);

    void deleteAllByGameCode(String gameCode);
}
