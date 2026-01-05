package com.single.agora_backend.dto.gpt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GptRequest {

    private List<Content> contents; // Gemini 규격

    @Getter
    @AllArgsConstructor
    public static class Content {
        private List<Part> parts;
    }

    @Getter
    @AllArgsConstructor
    public static class Part {
        private String text;
    }

    @Getter
    @AllArgsConstructor
    public static class Message { // 기존 Service 코드 호환용
        private String role;
        private String content;
    }

    // 메시지 리스트를 Gemini API 형식으로 변환
    public static GptRequest of(List<Message> messages) {
        StringBuilder fullText = new StringBuilder();
        if (messages != null) {
            for (Message m : messages) {
                fullText.append(m.getRole()).append(": ").append(m.getContent()).append("\n");
            }
        } else {
            fullText.append("user: Hello"); // 기본값
        }

        Part part = new Part(fullText.toString());
        Content content = new Content(List.of(part));
        return new GptRequest(List.of(content));
    }
}
