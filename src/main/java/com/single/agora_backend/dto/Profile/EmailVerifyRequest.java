package com.single.agora_backend.dto.Profile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerifyRequest {
    private String email;
    private Long userId;
    private String code;
}