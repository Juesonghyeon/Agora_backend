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

import java.net.URI; // [중요] 이거 import 필수
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
        // [수정 1] 하드코딩 제거 -> 설정 파일 값 사용
        String modelName = config.getModel(); // gemini-1.5-flash
        String apiKey = config.getApiKey();

        // [수정 2] 모델명에 'models/'가 포함되어 있는지 확인하여 URL 조립
        // 사용자가 properties에 "models/gemini..."로 적었을 수도 있고 그냥 "gemini..."로 적었을 수도 있음
        String finalModelName = modelName.startsWith("models/") ? modelName : "models/" + modelName;

        // [수정 3] URL 문자열 조합
        String urlString = "https://generativelanguage.googleapis.com/v1beta/"
                + finalModelName + ":generateContent?key=" + apiKey;

        log.info("Gemini API 호출 URL: https://generativelanguage.googleapis.com/v1beta/{}:generateContent", finalModelName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        GeminiRequest geminiRequest = convertToGeminiRequest(messages);
        HttpEntity<GeminiRequest> entity = new HttpEntity<>(geminiRequest, headers);

        try {
            // [중요 수정 4] String url 대신 URI.create(url) 사용
            // 이유: String으로 넘기면 RestTemplate이 내부적으로 한 번 더 인코딩을 시도하여
            // 콜론(:)이나 특수문자가 깨질 수 있습니다. URI 객체로 넘기면 있는 그대로 요청합니다.
            ResponseEntity<GeminiResponse> response = restTemplate.exchange(
                    URI.create(urlString),
                    HttpMethod.POST,
                    entity,
                    GeminiResponse.class
            );

            if (response.getBody() == null ||
                    response.getBody().getCandidates() == null ||
                    response.getBody().getCandidates().isEmpty()) {
                throw new RuntimeException("Gemini로부터 빈 응답을 받았습니다.");
            }

            return response.getBody()
                    .getCandidates().get(0)
                    .getContent()
                    .getParts().get(0)
                    .getText();

        } catch (HttpClientErrorException e) {
            log.error("API 호출 실패! 상태코드: {}, 응답바디: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Gemini API 호출 실패: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("예상치 못한 에러 발생", e);
            throw new RuntimeException("시스템 오류: " + e.getMessage());
        }
    }

    // --- 변환 로직 (기존과 동일) ---
    private GeminiRequest convertToGeminiRequest(List<GptRequest.Message> messages) {
        List<GeminiContent> contents = new ArrayList<>();
        for (GptRequest.Message msg : messages) {
            String role = "user".equals(msg.getRole()) ? "user" : "model";
            GeminiPart part = new GeminiPart(msg.getContent());
            contents.add(new GeminiContent(role, List.of(part)));
        }
        return new GeminiRequest(contents);
    }

    // --- DTO (기존과 동일) ---
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
        }
    }
}