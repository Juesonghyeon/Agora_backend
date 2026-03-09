package com.single.agora_backend.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "users")
@Getter @Setter @NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    private String password;
    private boolean isOnline = false;
    private LocalDateTime lastActivity;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfile userProfile;
    public User(String username, String password) { this.username = username; this.password = password; }
}