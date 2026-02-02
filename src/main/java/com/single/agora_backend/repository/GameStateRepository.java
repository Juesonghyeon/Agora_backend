package com.single.agora_backend.repository;

import com.single.agora_backend.entity.GameState;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GameStateRepository extends JpaRepository<GameState, Long> {
    Optional<GameState> findByParticipationCode(String participationCode);
}