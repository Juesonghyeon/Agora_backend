package com.single.agora_backend.repository;

import com.single.agora_backend.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByParticipationCode(String code);
    boolean existsByParticipationCode(String code);
}
