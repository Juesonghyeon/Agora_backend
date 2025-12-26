package com.single.agora_backend.dto.gpt;

import com.single.agora_backend.dto.gpt.GptRequest;
import lombok.Getter;

import java.util.List;

@Getter
public class TestGptRequest {

    private List<GptRequest.Message> messages;
}
