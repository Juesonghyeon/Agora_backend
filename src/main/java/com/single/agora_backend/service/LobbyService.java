package com.single.agora_backend.service;

import com.single.agora_backend.entity.LobbyPlayer;
import com.single.agora_backend.entity.User;
import com.single.agora_backend.repository.LobbyPlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LobbyService {

    private final LobbyPlayerRepository lobbyPlayerRepository;

    @Transactional
    public void joinLobby(User user) {
        // 이미 로비에 있으면 중복 생성 방지
        if (lobbyPlayerRepository.existsByUserId(user.getId())) return;

        // "DEFAULT"는 로비용 코드. 유저의 실제 사진 경로(getProfileImageUrl)를 함께 저장
        lobbyPlayerRepository.save(
                new LobbyPlayer("DEFAULT", user.getId(), user.getUsername(), user.getUserProfile().getProfileImageUrl())
        );
    }

    public List<LobbyPlayer> getPlayers() {
        return lobbyPlayerRepository.findByGameCode("DEFAULT");
    }

    @Transactional
    public void leaveLobby(User user) {
        lobbyPlayerRepository.deleteByUserId(user.getId());
    }
}