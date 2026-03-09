package com.single.agora_backend.dto.Profile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequest {
    private Long userId;
    private String email;
}