package com.single.agora_backend.repository;

import com.single.agora_backend.entity.GameParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameParticipantRepository extends JpaRepository<GameParticipant, Long> {

    // 1. 특정 게임방의 모든 참가자 목록 조회 (사이드바용)
    List<GameParticipant> findByGameCode(String gameCode);

    // 2. 특정 유저가 해당 방에 있는지 확인 (중복 입장 방지용)
    boolean existsByGameCodeAndUserId(String gameCode, Long userId);

    // 3. 특정 유저의 참가 정보 조회
    Optional<GameParticipant> findByGameCodeAndUserId(String gameCode, Long userId);

    // 4. 유저가 방을 나갈 때 삭제 처리
    void deleteByGameCodeAndUserId(String gameCode, Long userId);

    // 5. 게임이 종료되어 방이 없어질 때 모든 참가자 삭제
    void deleteAllByGameCode(String gameCode);

    long countByGameCode(String gameCode);
}