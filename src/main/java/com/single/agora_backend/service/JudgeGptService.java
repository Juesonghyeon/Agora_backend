package com.single.agora_backend.service;

import com.single.agora_backend.dto.gpt.GptRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JudgeGptService {

    private final GptService gptService;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public Map<String, Integer> scoreDebate(String topic, String team1Claim, String team2Claim) {
        String prompt = String.format("""
                주제: %s
                
                [팀1]: %s
                [팀2]: %s
                
                위 주장을 평가하여 점수(0~100)를 매겨라.
                1. 주제와 전혀 상관없는 헛소리라면 0점.
                2. 논리적일수록 높은 점수.
                
                응답은 오직 JSON 형식으로만 해라.
                예시: {"team1": 85, "team2": 10}
                """, topic, team1Claim, team2Claim);

        String jsonResult = gptService.ask(List.of(
                new GptRequest.Message("system", "너는 JSON만 출력하는 심사위원이다."),
                new GptRequest.Message("user", prompt)
        ));



        // 마크다운 제거
        jsonResult = jsonResult.replace("```json", "").replace("```", "").trim();
        return objectMapper.readValue(jsonResult, Map.class);
    }
}