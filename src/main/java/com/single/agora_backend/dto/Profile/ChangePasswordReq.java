package com.single.agora_backend.dto.Profile;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChangePasswordReq {
    private Long userId;
    private String oldPassword;
    private String newPassword;
}