package com.single.agora_backend.dto.Profile;

import lombok.Getter;

@Getter
public class DmSendRequest {
    private Long senderId;
    private Long receiverId;
    private String content;
}