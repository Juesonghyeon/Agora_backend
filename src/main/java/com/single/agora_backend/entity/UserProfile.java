package com.single.agora_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_profiles")
@Getter @Setter @NoArgsConstructor
public class UserProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // 🌟 이메일 중복 방지 제약조건 추가
    @Column(unique = true)
    private String email;

    private String profileImageUrl = "/uploads/profiles/default.jpg";
    private String verificationCode;
    private boolean emailVerified = false;
}