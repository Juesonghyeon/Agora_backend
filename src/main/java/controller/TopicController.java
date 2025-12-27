package com.single.agora_backend.controller;

import com.single.agora_backend.dto.TopicRequest;
import com.single.agora_backend.entity.Topic;
import com.single.agora_backend.service.TopicService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping("/user/{userId}")
    public List<Topic> getTopicsByUser(@PathVariable Long userId) {
        return topicService.getTopicsByUser(userId);
    }

    @PostMapping
    public Topic createTopic(@RequestBody TopicRequest request) {
        return topicService.createTopic(request);
    }

    @PatchMapping("/{topicId}")
    public Topic updateTopic(@PathVariable Long topicId,
                             @RequestBody TopicRequest request) {
        return topicService.updateTopic(topicId, request);
    }

    @DeleteMapping("/{topicId}")
    public void deleteTopic(@PathVariable Long topicId) {
        topicService.deleteTopic(topicId);
    }
}
