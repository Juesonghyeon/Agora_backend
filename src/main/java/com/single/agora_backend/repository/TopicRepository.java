package com.single.agora_backend.repository;

import com.single.agora_backend.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    List<Topic> findAllByUserId(Long userId);

    boolean existsByParticipationCode(String participationCode);

    // ⭐ 추가 (이거 하나 때문에 에러 난 거임)
    Optional<Topic> findByParticipationCode(String participationCode);
}
