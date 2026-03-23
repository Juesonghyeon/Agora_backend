package com.single.agora_backend.service;

import com.single.agora_backend.dto.gpt.GptRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JudgeGptService {

    private final GptService gptService;

    public boolean validateTopic(String topic) {
        String prompt = String.format("""
            당신은 서비스의 안전을 책임지는 콘텐츠 필터링 AI입니다.
            사용자가 제시한 다음 토론 주제가 대중적인 서비스에서 다루기에 안전하고 적절한지 엄격하게 평가하십시오.
            
            [절대 금지 주제]
            - 나치즘, 파시즘 등 극단주의 및 반인륜적 이념
            - 소아성애, 강간 등 성범죄 및 민감한 성적 콘텐츠
            - 살인, 테러, 마약 등 심각한 범죄 모의 및 옹호
            - 특정 인종, 성별, 종교, 장애에 대한 심각한 혐오 발언 및 차별
            - 자해 및 자살 조장
            
            평가 주제: [%s]
            
            위 주제가 건전하고 윤리적인 토론이 가능한 주제라면 오직 'VALID'라고만 대답하고,
            위험하거나 금지된 규정을 위반하는 주제라면 오직 'INVALID'라고만 대답하십시오. 다른 설명은 절대 덧붙이지 마십시오.
            """, topic);

        try {
            String response = gptService.ask(List.of(
                    new GptRequest.Message("user", prompt)
            ));

            // AI의 응답이 VALID인지 확인 (공백 및 대소문자 무시)
            return response != null && response.trim().equalsIgnoreCase("VALID");
        } catch (Exception e) {
            log.error("AI 주제 검증 중 오류 발생 (주제: {})", topic, e);
            // AI 통신 장애 등 예외 발생 시, 보수적으로(안전을 위해) 거절 처리하거나
            // 서비스 정책에 따라 일단 통과시킬 수 있습니다. 여기서는 거절(false)로 처리합니다.
            return false;
        }
    }

    public String judgeDebate(String topic, String t1History, String t2History) {
        String prompt = String.format("""
            당신은 '세계 토론 대회'의 수석 심사위원입니다. 주제: [%s]
            
            [심사 미션]
            제공된 BLUE 팀(Team 1)과 RED 팀(Team 2)의 토론 기록을 바탕으로 논리적 우위를 점한 승자를 판정하십시오.
            
            [심사 규칙]
            1. 중립적인 태도는 버리고, 반드시 'BLUE 팀' 또는 'RED 팀' 중 하나를 승자로 선언하십시오.
            2. 패배한 팀의 논리적 결함(반박 실패, 근거 부족 등)을 날카롭게 지적하십시오.
            3. 오직 토론 과정에서의 '설득력'과 '논증력'만 평가하십시오.
            
            [토론 내용]
            🔵 BLUE 팀(Team 1) 발언 기록:
            %s
            
            🔴 RED 팀(Team 2) 발언 기록:
            %s
            
            [출력 형식 - 반드시 마크다운 형식을 지킬 것]
            ## 🏆 최종 승자: [BLUE 팀 또는 RED 팀]
            
            ---
            ### 📊 심사 스코어 (10점 만점)
            - **BLUE 팀**: [점수]점
            - **RED 팀**: [점수]점
            
            ### 🔍 핵심 승리 요인
            1. **[반론의 날카로움]**: (승리 팀)이 상대의 ~한 주장을 ~한 논리로 완벽하게 무력화함.
            2. **[근거의 타당성]**: (승리 팀)이 제시한 ~한 예시가 주장을 뒷받침하는 데 결정적이었음.
            3. **[방어 성공]**: 상대의 공격에도 불구하고 자신의 ~한 논리를 끝까지 지켜냄.
            
            ### 📝 수석 심사위원 총평
            (전체적인 토론 흐름을 분석하고 승패가 갈린 결정적인 이유를 전문가 수준의 문체로 기술하십시오. 3문장 이내)
            """, topic, t1History, t2History);

        try {
            return gptService.ask(List.of(
                    new GptRequest.Message("system", "너는 공정하고 날카로운 세계 최고의 토론 심사위원이다. 마크다운으로 답변하라."),
                    new GptRequest.Message("user", prompt)
            ));
        } catch (Exception e) {
            log.error("AI 심사 실패", e);
            return "## ⚠️ 심사 지연 알림\nAI 심사위원과의 연결이 원활하지 않습니다. 양 팀 모두 훌륭한 토론을 보여주었습니다.";
        }
    }
}