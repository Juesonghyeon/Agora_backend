package com.single.agora_backend.service;

import com.single.agora_backend.dto.gpt.GptRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModeratorGptService {

    private final com.single.agora_backend.service.GptService gptService;

    public boolean isValidTopic(String topic) {
        String result = gptService.ask(List.of(
                new GptRequest.Message(
                        "system",
                        "너는 토론 진행자다. 토론에 적합하면 O, 부적합하면 X만 출력해라."
                ),
                new GptRequest.Message("user", topic)
        ));

        return result.trim().startsWith("O");
    }
}
