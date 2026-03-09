package com.single.agora_backend.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "friendships")
@Getter @Setter @NoArgsConstructor
public class Friendship {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "requester_id")
    private User requester;

    @ManyToOne @JoinColumn(name = "receiver_id")
    private User receiver;

    @Enumerated(EnumType.STRING)
    private FriendStatus status;

    public enum FriendStatus { PENDING, ACCEPTED }
}