package com.single.agora_backend.repository;

import com.single.agora_backend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // 특정 두 사용자 사이의 대화 내역을 시간순으로 가져오는 메서드 (나중에 채팅창 구현 시 필요)
    List<Message> findAllBySenderIdAndReceiverIdOrderBySentAtAsc(Long senderId, Long receiverId);
}