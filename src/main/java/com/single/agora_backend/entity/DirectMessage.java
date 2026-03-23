// 1. DirectMessage.java (Entity)
package com.single.agora_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "direct_messages")
@Getter @Setter
public class DirectMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;
    private Long receiverId;
    private String content;

    private LocalDateTime createdAt = LocalDateTime.now();
}