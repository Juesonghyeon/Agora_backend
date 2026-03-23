// FriendshipRepository.java
package com.single.agora_backend.repository;

import com.single.agora_backend.entity.Friendship;
import com.single.agora_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    // 수락된 친구 목록 조회 (요청자 혹은 수신자가 나인 경우)
    @Query("SELECT f FROM Friendship f WHERE (f.requester.id = :userId OR f.receiver.id = :userId) AND f.status = 'ACCEPTED'")
    List<Friendship> findAcceptedFriendsByUserId(@Param("userId") Long userId);

    // 나에게 온 대기 중인 신청 목록
    List<Friendship> findByReceiverIdAndStatus(Long receiverId, Friendship.FriendStatus status);

    @Query("SELECT f FROM Friendship f WHERE (f.requester.id = :userId AND f.receiver.id = :targetId) OR (f.requester.id = :targetId AND f.receiver.id = :userId)")
    Optional<Friendship> findRelation(@Param("userId") Long userId, @Param("targetId") Long targetId);

    void deleteByRequesterOrReceiver(User requester, User receiver);
}