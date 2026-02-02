package com.single.agora_backend.service;

import com.single.agora_backend.dto.gpt.GptRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModeratorGptService {

    private final GptService gptService;

    // 1차: 주제 적합성 판단
    public boolean isValidTopic(String topic) {
        String prompt = String.format("""
            주제: "%s"
            
            너는 토론 대회의 엄격한 심사위원이다. 아래 기준에 따라 주제를 심사해라.
            
            [부적합 기준 - 즉시 거절(X)]
            1. 증오 발언, 나치, 히틀러, 범죄 미화, 성적 표현, 심한 욕설.
            2. 토론이 불가능한 단순 사실 (예: "지구는 둥글다", "사과는 과일이다").
            3. 의미 없는 문자열 (예: "!나", "ㅁㄴㅇㄹ").
            
            [적합 기준(O)]
            1. 찬반 논쟁이 가능한 주제.
            2. 황당한 주제라도 논리가 성립되면 허용 (예: "민초 vs 반민초").
            3. vs, :, ~대 형식으로 대결구도를 잡을때 사용된 단어들이 부적절하지 않다면 허용한다.
            
            출력 형식: 적합하면 O, 부적합하면 X (설명 없이 딱 한 글자만 출력)
            """, topic);

        try {
            String result = gptService.ask(List.of(
                    new GptRequest.Message("system", "너는 O 또는 X로만 대답하는 판독기다."),
                    new GptRequest.Message("user", prompt)
            ));

            log.info("주제 심사: {} -> {}", topic, result);
            return result.trim().toUpperCase().contains("O");
        } catch (Exception e) {
            log.error("GPT 호출 오류", e);
            return false;
        }
    }

    // 2차: 주장 유사도 판단
    public boolean areClaimsTooSimilar(String topic, String claim1, String claim2) {
        String prompt = String.format("""
            주제: %s
            [A팀 주장]: %s
            [B팀 주장]: %s
            
            두 주장이 논리적으로 서로 반대인가? 아니면 둘 다 비슷한 소리를 하거나 주제와 무관한가?
            
            비슷하거나 엉뚱하다면: YES
            명확히 대립된다면: NO
            """, topic, claim1, claim2);

        String result = gptService.ask(List.of(
                new GptRequest.Message("system", "너는 논리 대립 판독기다. YES or NO only."),
                new GptRequest.Message("user", prompt)
        ));
        return result.trim().toUpperCase().contains("YES");
    }
}