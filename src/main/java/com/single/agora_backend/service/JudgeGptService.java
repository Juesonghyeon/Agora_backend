package com.single.agora_backend.service;

import com.single.agora_backend.dto.gpt.GptRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JudgeGptService {

    private final GptService gptService;
    private final ObjectMapper objectMapper;

    public Map<String, Integer> scoreDebate(String topic, String t1, String t2) {
        String prompt = "주제: " + topic + "\n블루: " + t1 + "\n레드: " + t2 + "\n점수를 JSON {\"team1\":점수, \"team2\":점수}로만 줘.";

        try {
            String res = gptService.ask(List.of(
                    new GptRequest.Message("system", "너는 심사위원이다. JSON만 말해라."),
                    new GptRequest.Message("user", prompt)
            ));

            if ("ERROR".equals(res)) return Map.of("team1", 50, "team2", 50);

            String cleanJson = res.replaceAll("(?s)```json|```", "").trim();
            return objectMapper.readValue(cleanJson, Map.class);
        } catch (Exception e) {
            return Map.of("team1", 50, "team2", 50);
        }
    }
}