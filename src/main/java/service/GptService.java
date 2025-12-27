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
        headers.setBearerAuth(config.getApiKey());

        GptRequest request = GptRequest.of(config.getModel(), messages);
        HttpEntity<GptRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<GptResponse> response;

        try {
            response = restTemplate.exchange(
                    config.getUrl(),
                    HttpMethod.POST,
                    entity,
                    GptResponse.class
            );
        } catch (Exception e) {
            throw new RuntimeException("GPT API 호출 실패", e);
        }

        if (response.getBody() == null
                || response.getBody().getChoices() == null
                || response.getBody().getChoices().isEmpty()) {
            throw new RuntimeException("GPT 응답이 비어있음");
        }

        return response.getBody()
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();
    }
}
