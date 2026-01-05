package com.single.agora_backend.service;

import com.single.agora_backend.dto.TopicRequest;
import com.single.agora_backend.entity.Topic;
import com.single.agora_backend.entity.User;
import com.single.agora_backend.repository.TopicRepository;
import com.single.agora_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    // ✅ 코드 문자셋 및 길이 수정 (12자리)
    private static final String CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 12;
    private final SecureRandom random = new SecureRandom();

    public TopicService(TopicRepository topicRepository, UserRepository userRepository) {
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
    }

    // 🔹 랜덤 코드 생성
    private String generateParticipationCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
        }
        return sb.toString();
    }

    // 🔹 중복 없는 유니크 코드 생성
    private String generateUniqueCode() {
        String code;
        do {
            code = generateParticipationCode();
        } while (topicRepository.existsByParticipationCode(code));
        return code;
    }

    // 🔹 전체 토픽 조회
    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    // 🔹 유저별 토픽 조회
    public List<Topic> getTopicsByUser(Long userId) {
        return topicRepository.findAllByUserId(userId);
    }

    // 🔹 토픽 생성
    public Topic createTopic(TopicRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        Topic topic = new Topic();
        topic.setTitle(request.getTitle());
        topic.setType(request.getType());
        topic.setScale(request.getScale());
        topic.setDifficulty(request.getDifficulty());

        // ✅ type이 "Ai"면 participationCode를 강제로 null로
        if ("Ai".equalsIgnoreCase(request.getType())) {
            topic.setParticipationCode(null);

        } else {
            // 기존 코드 유지
            if (request.getParticipationCode() != null && !request.getParticipationCode().isEmpty()) {
                topic.setParticipationCode(request.getParticipationCode());
            } else {
                topic.setParticipationCode(generateUniqueCode());
            }
        }

        topic.setUser(user);

        return topicRepository.save(topic);
    }

    // 🔹 토픽 수정
    public Topic updateTopic(Long topicId, TopicRequest request) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("토픽 없음"));

        topic.setTitle(request.getTitle());
        topic.setType(request.getType());
        topic.setScale(request.getScale());
        topic.setDifficulty(request.getDifficulty());

        // ✅ type이 "Ai"면 participationCode를 null로
        if ("Ai".equalsIgnoreCase(request.getType())) {
            topic.setParticipationCode(null);

        } else {
            // 기존 코드 유지
            if (request.getParticipationCode() != null && !request.getParticipationCode().isEmpty()) {
                topic.setParticipationCode(request.getParticipationCode());
            } else if (topic.getParticipationCode() == null || topic.getParticipationCode().isEmpty()) {
                topic.setParticipationCode(generateUniqueCode());
            }
        }

        return topicRepository.save(topic);
    }

    // 🔹 토픽 삭제
    public void deleteTopic(Long topicId) {
        topicRepository.deleteById(topicId);
    }
}
