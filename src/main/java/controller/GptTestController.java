package com.single.agora_backend.controller;

import com.single.agora_backend.dto.gpt.GptRequest;
import com.single.agora_backend.service.GptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class GptTestController {

    private final GptService gptService;

    @PostMapping("/gpt")
    public String testGpt(@RequestBody List<GptRequest.Message> messages) {
        return gptService.ask(messages);
    }
}
