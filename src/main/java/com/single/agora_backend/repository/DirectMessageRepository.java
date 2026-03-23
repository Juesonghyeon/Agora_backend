package com.single.agora_backend.repository;

import com.single.agora_backend.entity.DirectMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {
    // 두 사용자 간의 대화 기록을 시간순으로 가져오기
    @Query("SELECT m FROM DirectMessage m WHERE (m.senderId = :user1 AND m.receiverId = :user2) OR (m.senderId = :user2 AND m.receiverId = :user1) ORDER BY m.createdAt ASC")
    List<DirectMessage> findConversation(@Param("user1") Long user1, @Param("user2") Long user2);
}