package com.single.agora_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopicRequest {
    private String title;
    private String type; // MULTI, AI
    private String scale; // SMALL, MEDIUM, LARGE
    private String difficulty; // EASY, NORMAL, HARD
    private Long userId;
    private String participationCode; // ✅ 추가
}
