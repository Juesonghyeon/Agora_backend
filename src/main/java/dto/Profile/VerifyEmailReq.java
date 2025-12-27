package com.single.agora_backend.dto.Profile;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class VerifyEmailReq {
    private Long userId;
    private String code;
}