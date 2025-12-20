package com.single.agora_backend.repository;

import com.single.agora_backend.entity.LobbyPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LobbyPlayerRepository extends JpaRepository<LobbyPlayer, Long> {

    boolean existsByUserId(Long userId);

    void deleteByUserId(Long userId);

    List<LobbyPlayer> findByGameCode(String gameCode);
}
