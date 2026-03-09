package com.single.agora_backend.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

// UserProfile.java
@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
public class UserProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    private String email;
    private String profileImageUrl = "/uploads/profiles/default.jpg"; // 기본값 설정
    private String verificationCode;
    private boolean emailVerified = false;
}