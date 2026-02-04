package com.single.agora_backend.service;

import com.single.agora_backend.dto.gpt.GptRequest;
import com.single.agora_backend.entity.GameState;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamBuildingGptService {

    private final GptService gptService;
    private final ObjectMapper objectMapper;

    public Map<String, List<String>> clusterPlayers(String topic, Map<String, GameState.PlayerInfo> players) {
        StringBuilder sb = new StringBuilder();
        players.forEach((id, info) ->
                sb.append(String.format("- ID[%s]: %s\n", id, info.getInitialOpinion()))
        );

        // 프롬프트: 의견을 보고 팀을 나누되, 불가능하면 RESET 반환
        String prompt = String.format("""
            주제: %s
            참여자 의견 목록:
            %s
            
            위 사람들을 의견의 유사성에 따라 두 팀(team1, team2)으로 나누어라.
            
            [규칙]
            1. 의견이 너무 다양하거나, 한쪽으로 쏠려서 토론이 불가능하면 JSON 대신 "RESET"이라고만 답해라.
            2. 나눌 수 있다면 아래 JSON 형식을 지켜라. (ID는 대괄호 안의 문자열)
            {
              "team1": ["socketId1", ...],
              "team2": ["socketId2", ...]
            }
            """, topic, sb.toString());

        try {
            String response = gptService.ask(List.of(
                    new GptRequest.Message("system", "너는 공정한 토론 사회자다."),
                    new GptRequest.Message("user", prompt)
            ));

            if (response.contains("RESET")) {
                return null;
            }

            String json = response.replaceAll("(?s)```json|```", "").trim();
            return objectMapper.readValue(json, Map.class);

        } catch (Exception e) {
            log.error("AI 팀 빌딩 오류", e);
            return null; // 오류 시 리셋 처리
        }
    }
}