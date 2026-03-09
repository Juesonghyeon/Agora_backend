package com.single.agora_backend.dto.game;

public record LobbyPlayerResponse(
        Long userId,
        String username,
        String profileImageUrl
) {}