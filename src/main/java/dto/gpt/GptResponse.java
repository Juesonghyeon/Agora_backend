package com.single.agora_backend.dto.gpt;

import lombok.Getter;
import java.util.List;

@Getter
public class GptResponse {

    private List<Choice> choices;

    @Getter
    public static class Choice {
        private Message message;
    }

    @Getter
    public static class Message {
        private String role;
        private String content;
    }
}
