package com.single.agora_backend.dto.Profile;

import lombok.*;

// 프로필 정보 응답용
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ProfileDto {
    private Long userId;
    private String username;
    private String email;
    private String profileImageUrl;
    private boolean emailVerified;
}