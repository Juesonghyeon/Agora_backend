package com.single.agora_backend.service;

import com.single.agora_backend.dto.gpt.GptRequest;
import com.single.agora_backend.entity.GameState;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        // ⭐ 짬뽕/짜장 등 대립 구도를 완벽히 찢어놓기 위한 강압적 프롬프트
        String prompt = String.format("""
            주제: %s
            참여자 의견 목록:
            %s
            
            [임무]
            위 참여자들을 의견의 '차이점'에 주목하여 'team1'과 'team2'로 엄격하게 나누어라.
            
            [절대 규칙]
            1. '짬뽕' 선호자와 '짜장' 선호자처럼 서로 다른 입장을 가진 사람은 절대 같은 팀에 넣지 마라. 반드시 찢어라.
            2. 두 팀은 무조건 대립 관계여야 한다.
            3. 인원수는 최대한 균등하게 배분하되, 극단적인 경우 1:2 등도 허용한다.
            4. 모두의 의견이 완전히 똑같아서 대립 팀을 만들 수 없는 경우에만 "RESET"이라고 답변해라.
            5. 나눌 수 있다면 반드시 아래 JSON 형식만 정확히 출력해라.
            {
              "team1": ["socketId1", "socketId2"],
              "team2": ["socketId3"]
            }
            """, topic, sb.toString());

        try {
            String response = gptService.ask(List.of(
                    new GptRequest.Message("system", "너는 공정한 토론 팀 배정 전문가다. 너의 유일한 목적은 대립하는 두 팀을 분리하는 것이다."),
                    new GptRequest.Message("user", prompt)
            ));

            if (response.toUpperCase().contains("RESET")) {
                log.warn("AI가 대립 구도 형성 불가(RESET) 판정을 내렸습니다.");
                return null;
            }

            // JSON 부분만 정규식으로 안전하게 추출
            Pattern pattern = Pattern.compile("\\{.*\\}", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(response);

            if (matcher.find()) {
                String json = matcher.group();
                return objectMapper.readValue(json, Map.class);
            } else {
                throw new Exception("응답에서 유효한 JSON을 찾을 수 없음: " + response);
            }

        } catch (Exception e) {
            log.error("AI 팀 빌딩 오류", e);
            return null;
        }
    }
}