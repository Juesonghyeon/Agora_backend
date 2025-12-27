package com.single.agora_backend.dto.gpt;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GptRequest {

    private String model;
    private List<Message> messages;
    private int max_tokens;
    private double temperature;

    @Getter
    @AllArgsConstructor
    public static class Message {
        private String role;   // system / user
        private String content;
    }

    public static GptRequest of(String model, List<Message> messages) {
        return new GptRequest(model, messages, 300, 0.2);
    }
}
