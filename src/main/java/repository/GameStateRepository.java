package com.single.agora_backend.repository;

import com.single.agora_backend.entity.GameState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameStateRepository extends JpaRepository<GameState, Long> {
}
