package com.single.agora_backend.dto.Profile;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class SearchUserDto {
    private Long userId;
    private String username;
    private String profileImageUrl;
    private String relationStatus; // SELF, NONE, PENDING, ACCEPTED
}