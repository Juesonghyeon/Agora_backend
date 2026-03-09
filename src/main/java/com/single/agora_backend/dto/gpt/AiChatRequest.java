package com.single.agora_backend.dto.gpt;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class AiChatRequest {
    private String stage; // OPENING, EVIDENCE, REBUTTAL, CLOSING
    private String topicTitle;
    private String difficulty;
    private List<GptRequest.Message> history;
}