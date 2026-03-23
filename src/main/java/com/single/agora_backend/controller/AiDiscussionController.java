package com.single.agora_backend.controller;

import com.single.agora_backend.dto.gpt.AiChatRequest;
import com.single.agora_backend.service.AiDiscussionService;
import com.single.agora_backend.service.ModeratorGptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiDiscussionController {

    private final AiDiscussionService aiDiscussionService;
    private final ModeratorGptService moderatorGptService;

    // 1. 주제 검증 API
    @PostMapping("/validate-topic")
    public ResponseEntity<?> validateTopic(@RequestBody Map<String, String> request) {
        String topic = request.get("topic");
        boolean isValid = aiDiscussionService.validateTopic(topic);

        return ResponseEntity.ok(Map.of("isValid", isValid));
    }

    // 2. 토론 단계별 채팅 API
    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody AiChatRequest request) {
        // 발언 유효성 체크 (선택 사항: moderatorGptService.validateClaim 사용 가능)
        String content = aiDiscussionService.getAiResponse(
                request.getStage(),
                request.getTopicTitle(),
                request.getDifficulty(),
                request.getHistory()
        );
        return Map.of("content", content);
    }

    // 3. 최종 판정 API
    @PostMapping("/judge")
    public Map<String, String> judge(@RequestBody AiChatRequest request) {
        String content = aiDiscussionService.judgeDebate(
                request.getTopicTitle(),
                request.getHistory()
        );
        return Map.of("content", content);
    }

    @DeleteMapping("/delete-topic")
    public ResponseEntity<Void> deleteAiTopic(@RequestParam String title) {
        aiDiscussionService.deleteTopicByTitle(title);
        return ResponseEntity.ok().build();
    }
}