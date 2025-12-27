package com.single.agora_backend.service;

import com.single.agora_backend.config.OpenAiConfig;
import com.single.agora_backend.dto.gpt.GptRequest;
import com.single.agora_backend.dto.gpt.GptResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GptService {

    private final RestTemplate restTemplate;
    private final OpenAiConfig config;

    public String ask(List<GptRequest.Message> messages) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Authorization 헤더는 필요 없고, URL에 key 쿼리 사용
        String url = config.getUrl() + "?key=" + config.getApiKey();

        GptRequest request = GptRequest.of(messages);
        HttpEntity<GptRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<GptResponse> response;
        try {
            response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    GptResponse.class
            );
        } catch (Exception e) {
            throw new RuntimeException("Gemini API 호출 실패: " + e.getMessage(), e);
        }

        if (response.getBody() == null
                || response.getBody().getCandidates() == null
                || response.getBody().getCandidates().isEmpty()) {
            throw new RuntimeException("Gemini 응답이 비어있음");
        }

        return response.getBody()
                .getCandidates()
                .get(0)
                .getContent()
                .getParts()
                .get(0)
                .getText();
    }
}
