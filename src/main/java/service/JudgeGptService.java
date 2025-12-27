package com.single.agora_backend.service;

import com.single.agora_backend.dto.gpt.GptRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JudgeGptService {

    private final GptService gptService;

    public String judge(String topic, List<String> team1, List<String> team2) {
        String prompt = """
                주제: %s

                팀1 주장:
                %s

                팀2 주장:
                %s

                더 논리적이고 근거가 탄탄한 팀을 판정해라.
                """.formatted(
                topic,
                String.join("\n", team1),
                String.join("\n", team2)
        );

        return gptService.ask(List.of(
                new GptRequest.Message("system", "너는 공정한 토론 심사위원이다."),
                new GptRequest.Message("user", prompt)
        ));
    }
}
