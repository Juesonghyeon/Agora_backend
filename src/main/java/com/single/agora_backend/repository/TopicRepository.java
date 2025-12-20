package com.single.agora_backend.repository;

import com.single.agora_backend.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findAllByUserId(Long userId);
    boolean existsByParticipationCode(String participationCode);
}