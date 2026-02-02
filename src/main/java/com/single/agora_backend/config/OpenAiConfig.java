package com.single.agora_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAiConfig {

    @Value("${gemini.api.key}")
    private String apiKey;

    // 값이 없으면 gemini-1.5-flash를 기본값으로 사용
    @Value("${gemini.model:gemini-1.5-flash}")
    private String model;

    // 값이 없으면 구글 기본 주소 사용
    @Value("${gemini.url:https://generativelanguage.googleapis.com}")
    private String url;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getModel() {
        return model;
    }

    public String getUrl() {
        return url;
    }
}