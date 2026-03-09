package com.single.agora_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    @ManyToOne @JoinColumn(name = "receiver_id")
    private User receiver;
    private String content;
    private LocalDateTime sentAt = LocalDateTime.now();
}