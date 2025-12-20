package com.single.agora_backend.service;

import com.single.agora_backend.entity.LobbyPlayer;
import com.single.agora_backend.entity.User;
import com.single.agora_backend.repository.LobbyPlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LobbyService {

    private final LobbyPlayerRepository lobbyPlayerRepository;

    public void joinLobby(User user) {
        if (lobbyPlayerRepository.existsByUserId(user.getId())) return;

        lobbyPlayerRepository.save(
                new LobbyPlayer("DEFAULT", user.getId(), user.getUsername())
        );
    }

    public void leaveLobby(User user) {
        lobbyPlayerRepository.deleteByUserId(user.getId());
    }

    public List<LobbyPlayer> getPlayers() {
        return lobbyPlayerRepository.findByGameCode("DEFAULT");
    }
}
