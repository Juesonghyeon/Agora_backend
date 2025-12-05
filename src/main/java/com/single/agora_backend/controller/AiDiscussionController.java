package com.single.agora_backend.controller;

import com.single.agora_backend.entity.Topic;
import com.single.agora_backend.repository.TopicRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiDiscussionController {

    private final TopicRepository topicRepository;

    public AiDiscussionController(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    // 🔥 AI 토론 입장 API
    @PostMapping("/enter/{topicId}")
    public Map<String, Object> enterAiDiscussion(@PathVariable Long topicId) {

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("토픽 없음"));

        Map<String, Object> result = new HashMap<>();
        result.put("type", "success");
        result.put("topicId", topic.getId());
        result.put("title", topic.getTitle());

        return result;
    }
}
