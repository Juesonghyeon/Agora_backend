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

    public boolean isValidTopic(String topic) {
        // 프롬프트를 훨씬 관대하게 수정
        String prompt = String.format("""
            주제: "%s"
            이 주제가 토론하기에 괜찮은가? 
            범죄 조장, 심각한 욕설, 아예 의미 없는 자음 나열이 아니라면 웬만하면 "O"라고 답해라.
            조금이라도 토론의 여지가 있다면 "O"를 선택해라.
            """, topic);
        try {
            String res = simpleAsk(prompt);
            log.info("Topic Validation Result for [{}]: {}", topic, res);
            // 'X'가 명시적으로 포함된 게 아니면 다 통과시키는 전략
            return !res.equals("X");
        } catch (Exception e) {
            return true; // 에러 나면 그냥 통과시켜줌
        }
    }

    public boolean validateClaim(String topic, String claim) {
        String prompt = String.format("""
            주제: %s
            발언: %s
            이 발언이 한국어 문장으로서 의미가 있는가? 
            "ㅋㅋ", "ㅁㄴㅇㄹ" 같은 단순 나열이 아니라면 "O"라고 답해라.
            """, topic, claim);

        try {
            String res = simpleAsk(prompt);
            return !res.equals("X");
        } catch (Exception e) { return true; }
    }

    public boolean areClaimsTooSimilar(String topic, String claim1, String claim2) {
        // 정말 토론이 안 될 정도로 똑같은 말일 때만 리셋하도록 수정
        String prompt = String.format("""
            주제: %s
            A: %s
            B: %s
            두 주장의 내용이 거의 90%% 이상 일치해서 토론이 불가능한가? 
            내용이 서로 다르면 반드시 "NO"라고 답해라.
            """, topic, claim1, claim2);

        try {
            String res = simpleAsk(prompt);
            return res.equals("YES");
        } catch (Exception e) { return false; }
    }

    private String simpleAsk(String prompt) {
        String result = gptService.ask(List.of(
                new GptRequest.Message("system", "너는 관대한 판사다. 오직 한 글자(O, X, YES, NO)로만 대답해라."),
                new GptRequest.Message("user", prompt)
        )).trim().toUpperCase();

        // GPT가 "대답은 O입니다" 처럼 길게 말할 경우를 대비해 첫 글자만 추출
        if (result.length() > 0) {
            if (result.contains("O")) return "O";
            if (result.contains("X")) return "X";
            if (result.contains("YES")) return "YES";
            if (result.contains("NO")) return "NO";
        }
        return result;
    }
}