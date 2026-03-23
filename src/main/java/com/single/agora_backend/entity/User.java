package com.single.agora_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🌟 추가: 유저가 요청한 친구 관계들 자동 삭제
    @OneToMany(mappedBy = "requester", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Friendship> sentFriendships = new ArrayList<>();

    // 🌟 추가: 유저가 받은 친구 관계들 자동 삭제
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Friendship> receivedFriendships = new ArrayList<>();

    @Column(nullable = false, unique = true)
    private String username;
    private String password;

    private boolean isOnline = false;
    private LocalDateTime lastActivity;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfile userProfile;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}