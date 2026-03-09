package com.single.agora_backend.dto.Profile;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class FriendDto {
    private Long friendshipId;
    private Long friendId;
    private String username;
    private String profileImageUrl;
    private boolean online;
}