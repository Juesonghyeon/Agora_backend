package com.single.agora_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER) // LAZY → EAGER로 변경
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true, nullable = false)
    private com.single.agora_backend.entity.User user;

    private String email;
    private String profileImageUrl;
    private boolean emailVerified = false;
    private String verificationCode;
}
