package com.single.agora_backend.controller;

import com.single.agora_backend.dto.gpt.TestGptRequest;
import com.single.agora_backend.service.GptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class GptTestController {

    private final GptService gptService;

    @PostMapping("/gpt")
    public String testGpt(@RequestBody TestGptRequest request) {
        return gptService.ask(request.getMessages());
    }
}
