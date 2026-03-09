package com.single.agora_backend.service;

import com.single.agora_backend.config.OpenAiConfig;
import com.single.agora_backend.dto.gpt.GptRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GptService {

    private final RestTemplate restTemplate;
    private final OpenAiConfig config;

    public String ask(String userMessage) {
        GptRequest.Message message = new GptRequest.Message("user", userMessage);
        return ask(List.of(message));
    }

    public String ask(List<GptRequest.Message> messages) {
        String modelName = config.getModel();
        String apiKey = config.getApiKey();

        String finalModelName = modelName.startsWith("models/") ? modelName : "models/" + modelName;
        String urlString = "https://generativelanguage.googleapis.com/v1beta/"
                + finalModelName + ":generateContent?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        GeminiRequest geminiRequest = convertToGeminiRequest(messages);
        HttpEntity<GeminiRequest> entity = new HttpEntity<>(geminiRequest, headers);

        try {
            log.info("Gemini API 호출 중... (모델: {})", finalModelName);

            ResponseEntity<GeminiResponse> response = restTemplate.exchange(
                    URI.create(urlString),
                    HttpMethod.POST,
                    entity,
                    GeminiResponse.class
            );

            // [단계별 Null 체크 시작]
            GeminiResponse body = response.getBody();
            if (body == null || body.getCandidates() == null || body.getCandidates().isEmpty()) {
                log.error("Gemini 응답 바디가 비어있습니다. 전체 응답: {}", response);
                return "AI가 답변을 생성하지 못했습니다. (응답 바디 없음)";
            }

            GeminiResponse.Candidate firstCandidate = body.getCandidates().get(0);

            // 핵심 수정: Safety Filter 등으로 인해 content가 null일 수 있음
            if (firstCandidate.getContent() == null ||
                    firstCandidate.getContent().getParts() == null ||
                    firstCandidate.getContent().getParts().isEmpty()) {

                log.warn("Gemini가 부적절한 요청으로 판단했거나 안전 필터에 의해 답변을 차단했습니다. 응답 상태: {}", body);
                return "죄송합니다. 해당 주제나 발언은 AI 정책상 답변드리기 어렵습니다.";
            }

            // 모든 검증을 통과하면 텍스트 반환
            return firstCandidate.getContent().getParts().get(0).getText();

        } catch (HttpClientErrorException e) {
            log.error("API 호출 실패! 상태코드: {}, 상세내용: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return "API 호출 중 오류가 발생했습니다: " + e.getStatusCode();
        } catch (Exception e) {
            log.error("GPT 서비스 내부 에러 발생", e);
            return "시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        }
    }

    private GeminiRequest convertToGeminiRequest(List<GptRequest.Message> messages) {
        List<GeminiContent> contents = new ArrayList<>();
        for (GptRequest.Message msg : messages) {
            // Gemini는 시스템 프롬프트도 'user' 혹은 'model' 역할로 변환해서 보내야 할 때가 많습니다.
            // 여기서는 단순화를 위해 system도 user로 취급하거나 아래처럼 처리합니다.
            String role = "assistant".equals(msg.getRole()) ? "model" : "user";
            GeminiPart part = new GeminiPart(msg.getContent());
            contents.add(new GeminiContent(role, List.of(part)));
        }
        return new GeminiRequest(contents);
    }

    // --- Gemini 전용 DTO 구조 ---
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class GeminiRequest {
        private List<GeminiContent> contents;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class GeminiContent {
        private String role;
        private List<GeminiPart> parts;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class GeminiPart {
        private String text;
    }

    @Data
    static class GeminiResponse {
        private List<Candidate> candidates;

        @Data
        static class Candidate {
            private GeminiContent content;
            private String finishReason; // 답변 중단 이유 확인용
        }
    }
}