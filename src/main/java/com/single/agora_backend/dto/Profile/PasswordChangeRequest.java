package com.single.agora_backend.dto.Profile;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PasswordChangeRequest {
    private Long userId;
    private String currentPassword;
    private String newPassword;
}