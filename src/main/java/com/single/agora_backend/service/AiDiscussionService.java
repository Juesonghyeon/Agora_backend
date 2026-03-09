package com.single.agora_backend.service;

import com.single.agora_backend.dto.gpt.GptRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiDiscussionService {

    private final GptService gptService;

    public String getAiResponse(String stage, String title, String difficulty, List<GptRequest.Message> history) {
        String tone = difficulty.equals("쉬움")
                ? "옆집 친구처럼 친근하고 쉬운 단어를 쓰세요. 너무 정석적인 논리보다는 상식 선에서 대화하며 가끔 허점을 보이세요."
                : "매우 날카롭고 분석적인 태도를 유지하세요. 상대의 논리적 오류를 집요하게 파고드세요.";

        String stagePrompt = switch (stage) {
            case "OPENING" -> "상대의 주장에 반대주장을 말하고 대화를 시작하세요. 본인의 주장을 한 문장으로 먼저 던지세요.";
            case "EVIDENCE" -> "당신의 주장에 대한 근거를 대세요. 쉬운 예시를 들어 설명하세요.";
            case "REBUTTAL" -> "사용자의 공격에 대해 본인의 논리를 먼저 방어(변론)한 후, 사용자의 허점을 찔러 반격하세요.";
            case "CLOSING" -> "최종 발언입니다. 상대방보다 당신의 주장이 왜 더 현실적인지 강조하며 마무리하세요.";
            default -> "토론 규칙을 준수하세요.";
        };

        String systemPrompt = String.format("""
            당신은 토론가 AI입니다. 주제: [%s]
            규칙:
            1. %s
            2. %s
            3. 답변은 3~4문장으로 핵심만 전달하세요.
            """, title, tone, stagePrompt);

        List<GptRequest.Message> messages = new ArrayList<>();
        messages.add(new GptRequest.Message("system", systemPrompt));
        messages.addAll(history);

        return gptService.ask(messages);
    }

    public String judgeDebate(String title, List<GptRequest.Message> history) {
        StringBuilder sb = new StringBuilder();
        for (GptRequest.Message m : history) {
            sb.append(m.getRole().equals("user") ? "사용자: " : "AI: ").append(m.getContent()).append("\n\n");
        }

        String judgePrompt = String.format("""
            당신은 '세계 토론 대회'의 수석 심사위원입니다. 주제: [%s]
            
            [심사 미션]
            제공된 토론 기록을 바탕으로 논리적 우위를 점한 승자를 판정하십시오.
            
            [심사 규칙]
            1. 중립적인 태도는 버리고, 반드시 '사용자' 또는 'AI' 중 한 명을 승자로 선언하십시오.
            2. '둘 다 잘했다'는 식의 칭찬은 금지하며, 패배자의 논리적 결함(반박 실패, 근거 부족 등)을 날카롭게 지적하십시오.
            3. 캐릭터의 인지도(예: 아이언맨 vs 배트맨)가 아니라, 오직 토론 과정에서의 '설득력'과 '논증력'만 평가하십시오.
            
            [토론 내용]
            %s
            
            [출력 형식 - 반드시 마크다운 형식을 지킬 것]
            ## 🏆 최종 승자: [사용자 또는 AI]
            
            ---
            ### 📊 심사 스코어 (10점 만점)
            - **사용자**: [점수]점
            - **AI**: [점수]점
            
            ### 🔍 핵심 승리 요인
            1. **[반론의 날카로움]**: (사용자/AI)가 상대의 ~한 주장을 ~한 논리로 완벽하게 무력화함.
            2. **[근거의 타당성]**: (사용자/AI)가 제시한 ~한 예시가 주장을 뒷받침하는 데 결정적이었음.
            3. **[방어 성공]**: 상대의 공격에도 불구하고 자신의 ~한 논리를 끝까지 지켜냄.
            
            ### 📝 수석 심사위원 총평
            (전체적인 토론 흐름을 분석하고 승패가 갈린 결정적인 이유를 전문가 수준의 문체로 기술하십시오. 3문장 이내)
            """, title, sb.toString());

        return gptService.ask(List.of(new GptRequest.Message("user", judgePrompt)));
    }

    @Transactional
    public void deleteTopicByTitle(String title) {
        // 1:1 AI 토론 주제를 DB에서 찾아 삭제하는 로직
        // 예: topicRepository.deleteByTitle(title);
        System.out.println("토론 종료 후 삭제된 주제: " + title);
    }
}