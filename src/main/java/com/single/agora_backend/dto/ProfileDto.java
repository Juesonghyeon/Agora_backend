package com.single.agora_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileDto {
    private Long userId;
    private String username;
    private String email;
    private String profileImageUrl;
    private boolean emailVerified;
}
