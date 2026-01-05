package com.single.agora_backend.repository;

import com.single.agora_backend.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findAllByRoomId(Long roomId);
    void deleteAllByRoomId(Long roomId);
}
