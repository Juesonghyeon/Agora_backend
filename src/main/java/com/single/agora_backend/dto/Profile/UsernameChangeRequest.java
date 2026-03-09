package com.single.agora_backend.dto.Profile;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UsernameChangeRequest {
    private Long userId;
    private String password;
    private String newUsername;
}