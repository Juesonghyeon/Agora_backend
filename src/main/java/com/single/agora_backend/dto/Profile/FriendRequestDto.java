package com.single.agora_backend.dto.Profile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendRequestDto {
    private Long userId;
    private Long targetId;
}