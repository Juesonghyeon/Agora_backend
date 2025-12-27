package com.single.agora_backend.dto.Profile;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChangeUsernameReq {
    private Long userId;
    private String newUsername;
}